package main

import (
	"database/sql"
	"fmt"
	_ "github.com/lib/pq"
	"log"
	"os"
	"reflect"
	"strconv"
)

var con *sql.DB

type migration struct {
	Tx *sql.Tx
}

func versionsAvailable() int {
	for i := 1; i <= 100; i++ {
		var m migration
		v := reflect.ValueOf(&m).MethodByName("Migration_" + strconv.Itoa(i))
		if v.IsValid() == false {
			i--
			return i
		}
	}
	return 0
}

func dbVersionNumber(con *sql.Tx) (int, error) {
	_, err := con.Exec("create table if not exists version_number (version_number integer)")
	if err != nil {
		return 0, err
	}
	row := con.QueryRow("SELECT version_number FROM version_number")
	var id int
	err = row.Scan(&id)
	if err == sql.ErrNoRows {
		_, err = con.Exec("insert into version_number values(1)")
		if err != nil {
			return 0, err
		}
		row := con.QueryRow("SELECT version_number FROM version_number")
		var id int
		err = row.Scan(&id)
		return id, err
	} else {
		return id, err
	}
	return id, err
}

func updateVersionNumber(con *sql.Tx, version int) error {
	_, err := con.Exec("delete from version_number")
	if err != nil {
		return err
	}
	_, err = con.Exec("insert into version_number (version_number) values(" + strconv.Itoa(version) + ")")
	return err
}

func main() {
	// Open db
	var err error
	connStr := os.Args[1]
	con, err = sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal(err)
	}
	defer con.Close()
	tx, err := con.Begin()
	if err != nil {
		log.Fatal(err)
	}

	// Get version number
	versionNumber, err := dbVersionNumber(tx)
	if err != nil {
		log.Fatalf("Couldn't get version of db: %v", err)
	}
	fmt.Println("Current db version number:", versionNumber)

	// Get number of versions in script
	versionsAvailable := versionsAvailable()
	fmt.Printf("Versions available: 1-%d\n", versionsAvailable)
	fmt.Println("---")

	// Get migration functions
	for i := versionNumber; i <= versionsAvailable; i++ {
		defer func() {
			if e := recover(); e != nil {
				fmt.Println("Rolling back")
				err := tx.Rollback()
				if err != nil {
					fmt.Println("Rollback error: ", err)
				}
				fmt.Println("Failed migration:", e)
				os.Exit(1)
			}
		}()
		m := migration{tx}
		fmt.Printf("### Running Migration_%d\n", i)
		reflect.ValueOf(&m).MethodByName("Migration_" + strconv.Itoa(i)).Call([]reflect.Value{})
		err := updateVersionNumber(tx, i+1)
		if err != nil {
			panic(err)
		}
	}

	tx.Commit()
}

func (m migration) Migration_1() {
	_, err := m.Tx.Exec("create table sequence (seq_name varchar(50) not null primary key, seq_count int)")
	if err != nil {
		panic(err)
	}
	_, err = m.Tx.Exec("insert into sequence (seq_name, seq_count) values('SEQ_GEN', 1)")
	if err != nil {
		panic(err)
	}
	_, err = m.Tx.Exec("create table userentity (username varchar(50) not null primary key, admin bool, password varchar(200) not null)")
	if err != nil {
		panic(err)
	}
}

func (m migration) Migration_2() {
	_, err := m.Tx.Exec("create table postentity (id integer not null primary key, content text, adminedited boolean not null, subject varchar(300), threadId varchar(100), created decimal not null, modified decimal not null, user_reference varchar(50) not null references userentity(username))")
	if err != nil {
		panic(err)
	}
	_, err = m.Tx.Exec("create table post_tags (post_id integer references postentity(id), tag_text varchar(20))")
	if err != nil {
		panic(err)
	}
}

func (m migration) Migration_3() {
	_, err := m.Tx.Exec("create table threadentity (id varchar(100) not null primary key, numPosts decimal not null, latestPost_reference integer not null references postentity(id), rootPost_reference integer references postentity(id))")
	if err != nil {
		panic(err)
	}
	_, err = m.Tx.Exec("create table thread_posts (thread_id varchar(100) not null references threadentity(id), post_id integer not null references postentity(id))")
	if err != nil {
		panic(err)
	}
}

func (m migration) Migration_4() {
	_, err := m.Tx.Exec("create table pushids (client_id varchar(500))")
	if err != nil {
		panic(err)
	}
}

func (m migration) Migration_5() {
	_, err := m.Tx.Exec("alter table userentity add request_pw_reset bool")
	if err != nil {
		panic(err)
	}
}

func (m migration) Migration_6() {
	_, err := m.Tx.Exec("alter table userentity add recovery_email varchar(200) unique")
	if err != nil {
		panic(err)
	}
}
