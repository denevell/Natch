package main

import (
	"strconv"
	"database/sql"
	_ "github.com/lib/pq"
	"reflect"
	"fmt"
	"log"
)

type migration string

func main() {
	// Open db
	con, err := sql.Open("postgres", "postgres://denevell:user@localhost:5432/testnatch")
	if err != nil {
		log.Fatal(err)
	}
	defer con.Close()

	// Get version number
	versionNumber, err := dbVersionNumber(con)
	if err != nil {
		log.Fatalf("Couldn't get version of db: %v", err)
	}

	fmt.Println("Current db version number:", versionNumber)

	// Get migration functions
	for i := versionNumber; i <= 2; i++ {
		var m migration
		reflect.ValueOf(&m).MethodByName("Migration_"+strconv.Itoa(i)).Call([]reflect.Value{})
	}
}

func (m migration) Migration_1() {
	fmt.Println("One")
}

func (m migration) Migration_2() {
	fmt.Println("Two")
}

func dbVersionNumber(con *sql.DB) (int, error) {
	row := con.QueryRow("SELECT version_number FROM info")
	var id int 
	err := row.Scan(&id)
	return id, err;
}
