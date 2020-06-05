(ns codenalysis.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [ajax.core :refer [GET POST]]))

(defonce history (atom []))
(defonce pages (atom 1))
(defonce username (atom ""))

(declare fetch)

(defn handler [response]
  (let [n (count response)]
    (swap! history into response)
    (if (= n 100)
      (do (swap! pages inc)
          (fetch @username @pages))
      (prn (count @history)))))

(defn fetch
  [user page]
  (GET (str "http://api.github.com/users/" user "/repos")
    {:handler handler
     :params {:page     page
              :per_page 100}}))

(defn get-user-history!
  [user]
  (reset! history [])
  (reset! pages 1)
  (reset! username user)
  (fetch user 1))

(defn history-panel []
  [:div
   [:h3 (interleave (map #(% "full_name") @history) (repeat [:br]))]])

(defn start []
  (reagent/render-component [history-panel]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))

