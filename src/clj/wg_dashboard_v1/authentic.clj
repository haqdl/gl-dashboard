(ns wg_dashboard_v1.authentic
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [clj-http.client :as http]
            [clj-oauth2.client :as oauth2]
            [ring.util.response :as response]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer (defroutes GET context)]
            [ring.adapter.jetty :as jetty]
            [cheshire.core :refer [parse-string]]
            [clojure.string :as string]))

(def config (edn/read-string (slurp (io/resource "config.edn"))))

(def oauth-params (merge {;; These should be set in the oauth-params.edn file
                          :redirect-uri "http://localhost:8000/oauth2callback"
                          :client-id "*google-client-id*"
                          :client-secret "*google-client-secret*"
                          :scope ["https://www.googleapis.com/auth/userinfo.email"]
                          ;; These don't need changes
                          :authorization-uri "https://accounts.google.com/o/oauth2/auth"
                          :access-token-uri "https://accounts.google.com/o/oauth2/token"
                          :access-query-param :access_token
                          :grant-type "authorization_code"
                          :access-type "online"
                          :approval_prompt ""
                          :hd "appsmiths.net"}
                         (:oauth-params config)))

(def auth-request (oauth2/make-auth-request oauth-params))

(defn save-token [req]
  (let [token (oauth2/get-access-token oauth-params
                                       (:params req)
                                       auth-request)
        token-info (:body (http/get "https://www.googleapis.com/oauth2/v1/tokeninfo"
                                    {:query-params {:access_token (:access-token token)}
                                     :as :json}))]
    ;(with-open [out (io/writer (io/file (:token-directory config "/tmp") (:email token-info)))]
    ;  (pprint (:email token-info))
      ;(pprint {:token token
      ;           :info token-info}
      ;        out))
    (if (string/ends-with? (:email token-info) "@appsmiths.net")
      (response/redirect (:success-url config "/"))
      (response/redirect (:notfound-url config "/")))))


(defn- google-user-email [access-token]
  (let [response (oauth2/get "https://www.googleapis.com/oauth2/v1/userinfo" {:oauth access-token})]
    (get (parse-string (:body response)) "email")))


(defroutes authentication-controller
           (GET "/" []
             (response/response "No browsable content on this server"))
           (GET "/google-oauth" []
             (response/redirect (:uri auth-request)))
           (GET "/oauth2callback" []
             save-token))

(defn -main [& args])


;(defn -main []
;  (jetty/run-jetty (-> authentication-controller
;                       wrap-keyword-params
;                       wrap-params)
;                   {:port (:port config 4449)}))
