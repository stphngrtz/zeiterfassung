# Zeiterfassung
Hierbei handelt es sich um ein kleines Projekt, mit dem ich Erfahrungen mit der App-Entwicklung (Ionic) sowie Docker auf AWS sammeln möchte. Ein netter Nebeneffekt ist, dass ich tatsächlich Leute kenne, die das Ergebnis des Projektes nutzen wollen. Dennoch, der Fokus liegt darauf, dass ich etwas lernen möchte - andernfalls würde man die ein oder andere Anforderung vielleicht anders lösen ;)

## Während der Entwicklung

MongoDB mit veröffentlichtem Port und gemappten Verzeichnis für die Daten.
```
> docker run -d  \
    --name mongo \
    -p 27017:27017 \
    -v /Users/stephan/Development/Zeiterfassung/backend/data:/data/db \
    mongo
```

Mongo-Express mit Link zur MongoDB inkl. Basic Authentication.
```
> docker run -d \
    --name mongo-express \
    --link mongo:mongo \
    -p 8081:8081 \
    -e ME_CONFIG_BASICAUTH_USERNAME="user" \
    -e ME_CONFIG_BASICAUTH_PASSWORD="password" \
    mongo-express
```

## Amazon Webservices

### Aufsetzer der EC2 Instanz
```
> docker-machine create -d amazonec2 --amazonec2-region eu-central-1 --amazonec2-ami ami-232aea4c zeiterfassung-1
```
Aktuell (Stand Dezember 2016) nutzt der AWS Treiber von Docker ein fehlerhaftes Ubuntu Image, wodurch der Docker Deamon auf der Instanz nicht startet. Das angegebene AMI entspricht Ubuntu 16.04.

### Anlegen sowie Hinzufügen einer Security Group für Port 4567
```
> aws ec2 create-security-group --group-name p4567 --description "Allow port 4567 inbound"
> aws ec2 authorize-security-group-ingress --group-name p4567 --protocol tcp --port 4567 --cidr 0.0.0.0/0
> aws ec2 describe-instances --filters "Name=tag:Name,Values=zeiterfassung-1" | grep InstanceId
> aws ec2 describe-security-groups --group-name docker-machine | grep GroupId
> aws ec2 describe-security-groups --group-name p4567 | grep GroupId
> aws ec2 modify-instance-attribute --instance-id <EC2_INSTANCE_ID> --groups "<SG_DOCKER_ID>" "SG_P4567_ID"
```

### Verbindung zur Instanz herstellen, MongoDB Daten-Verzeichnis anlegen und MongoDB starten
```
> docker-machine ssh zeiterfassung-1
$ sudo mkdir -p /usr/local/var/mongodb
$ sudo docker run -d  \
    --name mongo \
    -v /usr/local/var/mongodb:/data/db \
    mongo
```

### Mongo-Express starten (funktioniert bisher alles?)
```
$ sudo docker run -d \
    --name mongo-express \
    --link mongo:mongo \
    -p 4567:8081 \
    -e ME_CONFIG_BASICAUTH_USERNAME="user" \
    -e ME_CONFIG_BASICAUTH_PASSWORD="password" \
    mongo-express
```

Sobald Mongo-Express gestartet ist, kann von außen darauf zugegriffen werden!
```
> aws ec2 describe-instances --filters "Name=tag:Name,Values=zeiterfassung-1" | grep PublicDnsName
> safari <PUBLIC_DNS_NAME>:4567
```

Anschließend sollte Mongo-Express wieder gestoppt werden. Schließlich brauchen wir den Port für unsere Anwendung ;)
```
$ sudo docker stop mongo-express
```

### Backend-Image bauen, auf die EC2 Instanz transferieren und starten
```
> mvn clean package
> docker build -t zeiterfassung-backend .
> docker save -o zeiterfassung-backend.tar zeiterfassung-backend:latest
> docker-machine scp zeiterfassung-backend.tar zeiterfassung-1:/tmp
> docker-machine ssh zeiterfassung-1
$ sudo docker load -i /tmp/zeiterfassung-backend.tar
$ sudo docker run -d \
    --name zeiterfassung-backend \
    --link mongo:mongo \
    -p 4567:4567 \
    zeiterfassung-backend --db-host mongo
```

## Frontend bauen und ausprobieren
```
> rm -r node_modules/*
> rm -r www/*
> npm install
> ionic serve
```
In den Einstellungen muss als URL der public DNS Name der EC2 Instanz eingetragen werden. Anschließend sollte das Frontend wie erwartet mit dem Backend kommunizieren können.

### Frontend aufs Handy übertragen

#### iOS
```
ionic platform add ios
ionic resources
ionic build ios
```

Für Details zum Deployment auf iOS ohne Apple Developer Account, siehe folgenden Link: http://blog.ionic.io/deploying-to-a-device-without-an-apple-developer-account/

Kurzform:
- platforms/ios/Zeiterfassung.xcodeproj mit XCode öffnen
- Beim Projekt unter General > Signing das Personal Team auswählen
- Neben dem Run Button das Handy als Device auswählen
- Run!

#### Android
```
> ionic platform add android
> ionic resources
> ionic run android --device
```

Das Handy muss via USB verbunden sein, der Entwickler Modus sowie USB-Debugging muss aktiviert sein. Darüber hinaus muss natürlich das Android SDK (24) installiert sein.

Den Entwickler Modus auf dem Handy aktiviert man, indem man in den Einstellungen bzw. Geräteinformationen die Buildnummer 7x antippt. Nach 2-3x antippen sollte sich eine Information darüber einblenden, wie oft man noch tippen muss, um den Entwickler Modus zu aktivieren.

Bei erfolgreich aktiviertem Entwickler Modus sowie installiertem Android SDK schreibt der folgende Befehl ein Device aus.
```
> adb devices
```

Aktuell gibt es anscheinend ein Problem bei den Ressourcen für die Android Plattform. Siehe https://github.com/driftyco/ionic-cli/issues/1166

Folgendes hat bei mir geholfen:
```
> ionic platform rm android
> ionic platform add https://github.com/apache/cordova-android.git#master
> ionic resources --icon
> ionic run android --device
```
