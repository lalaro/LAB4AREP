FROM openjdk:19

WORKDIR /usrapp/bin

ENV PORT=6000

COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency
COPY target/classes/archivesPractice /app/static

CMD ["java","-cp","./classes:./dependency/*","edu.escuelaing.app.AppSvr.server.WebApplication"]