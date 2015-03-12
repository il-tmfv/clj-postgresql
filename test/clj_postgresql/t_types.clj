(ns clj-postgresql.t-types
  (:use midje.sweet)
  (:require [clj-postgresql.core :as pg]
            [clj-postgresql.types]
            [clojure.java.jdbc :as jdbc]))

(def test-data
  {"x" 42 "a" [4 3 2]})

(def test-table-name (str "test_json_" (rand-int 999)))

(facts "Write and read json and jsonb"
       (jdbc/with-db-transaction [tx (pg/spec)]
         (jdbc/db-do-commands tx (str "CREATE TEMP TABLE " test-table-name " (json_field json, jsonb_field jsonb)"))
         (fact (first (jdbc/insert! tx test-table-name {:jsonb_field test-data :json_field test-data}))
               => {:jsonb_field test-data :json_field test-data})
         (let [row  (first (jdbc/query tx (str "SELECT * FROM " test-table-name)))]
           (fact row => truthy)
           (fact (:json_field row) => test-data)
           (fact (:jsonb_field row) => test-data)
           )))

