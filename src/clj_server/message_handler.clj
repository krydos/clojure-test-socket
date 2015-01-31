(ns clj-server.message_handler
  (:gen-class)
  (:require [clojure.data.json :as json]))

(defn- is-message-from-devcie?
  [json-message]
  (if (:device json-message)
    true
    false))
(defn- process-message-from-device
  [json-message]
  (str "this was message from device" "\n"))

(defn- process-message-from-client 
  [json-message]
  (str "this was message from client" "\n"))

(defn process
  [msg]
  (let [json-message (try 
                       (json/read-str msg
                                      :key-fn keyword)
                       (catch Exception e (str (json/write-str {:error {:message (.getMessage e)}}) "\n")))]

    (println (string? json-message))
    (if (string? json-message)
      json-message
      (do
        (if (is-message-from-devcie? json-message)
          (process-message-from-device json-message)
          (process-message-from-client json-message))))))

    

