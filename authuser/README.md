# decoder-project-authuser

heroku create -a ead-authuser-ebo-prod --remote heroku-prod
git push heroku-prod prod:master

1- Adicionar add-on do cloud amqp
2- Adicionar um add-on em outro projeto: heroku addons:attach <instancia-que-criou-o-addon>::<add-on> --app <instancia-que-recebera-add-on>
heroku addons:attach ead-authuser-prod::CLOUDAMQP --app ead-course-prod
heroku addons:attach ead-authuser-prod::CLOUDAMQP --app ead-notification-prod
3- Adicionar as variaveis de ambiente tanto do config de MS quanto do config do config server
4- Adicionar add-on do postgre