(ns video-rental.test-schema
  (:require [clojure.java.jdbc :as j]))

(defn prepare [db]
  (j/execute! db (str "CREATE TABLE IF NOT EXISTS user ("
                      "id         integer auto_increment PRIMARY KEY,"
                      "bonus      integer NOT NULL DEFAULT 0)"))
  (j/execute! db (str "CREATE TABLE IF NOT EXISTS film ("
                      "tid        varchar(50) PRIMARY KEY,"
                      "title      varchar(200) NOT NULL,"
                      "year       integer NOT NULL)"))

  (j/execute! db (str "CREATE TABLE IF NOT EXISTS rent ("
                      "id         integer auto_increment PRIMARY KEY,"
                      "created    timestamp NULL DEFAULT NOW(),"
                      "userid     integer NOT NULL,"
                      "FOREIGN KEY (userid) REFERENCES user (id))"))

  (j/execute! db (str "CREATE TABLE IF NOT EXISTS rentfilm ("
                      "rentid    integer NOT NULL,"
                      "tid       varchar(50),"
                      "days      integer NOT NULL,"
                      "charge    decimal NULL,"
                      "created   timestamp NULL DEFAULT NOW(),"
                      "FOREIGN KEY (tid) REFERENCES film (tid),"
                      "FOREIGN KEY (rentid) REFERENCES rent (id))"))

  (j/execute! db "DELETE FROM rentfilm")
  (j/execute! db "DELETE FROM rent")
  (j/execute! db "DELETE FROM film")
  (j/execute! db "DELETE FROM user"))