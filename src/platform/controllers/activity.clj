(ns platform.controllers.activity
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [platform.util :as util]
            [noir.response :as resp]))


(defn right-wrong-quiz []
  (layout/render 
    "activity/right-wrong-quiz.html"
    {:bc_lessons "Aulas"
     :bc_new_lesson "Nova aula"
     :bc_new_act "Nova atividade"
     :title "Atividade: Certo ou Errado"
     :btn_add "Nova Questão"
     :btn_done "Feito"}))


(defn judge-image-quiz []
  (layout/render 
    "activity/judge-image-quiz.html" 
    {:bc_lessons "Aulas"
     :bc_new_lesson "Nova aula"
     :bc_new_act "Nova atividade"
     :title "Atividade: Julgando Imagens"
     :btn_add "Nova Questão"
     :btn_done "Feito"}))


(defn new-activity [] 
  (resp/redirect "/lessons/new"))
