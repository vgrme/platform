(ns platform.controllers.activity
  (:use 
      compojure.core)
  (:require 
      [platform.views.layout :as layout]
      [platform.util :as util]
      [noir.response :as resp]
      [platform.models.db :as db]))


(defn right-wrong-quiz []
  (layout/render
    "activity/right-wrong-quiz.html"
    {:bc_lessons      "Aulas"
     :bc_new_lesson   "Nova aula"
     :bc_new_act      "Nova atividade"
     :title           "Digite aqui o nome da sua atividade..."
     :btn_add         "Nova Questão"
     :btn_done        "Feito"}))


(defn judge-image-quiz [user-id]
  (layout/render
    "activity/judge-image-quiz.html"
    {:bc_lessons        "Aulas"
     :bc_new_lesson     "Nova aula"
     :bc_new_act        "Nova atividade"
     :title             "Digite aqui o nome da sua atividade..."
     :btn_add           "Nova Questão"
     :btn_done          "Feito"
     :btn_upload        "Carregar"
     :btn_fechar        "Fechar"
     :lbl_upload_image  "Carregar imagem"
     :thumb-prefix      "thumb_"
     :page-owner        user-id
     :pictures (db/images-by-user user-id)}))

(defn new-activity []
  (resp/redirect "/nova/aula"))
