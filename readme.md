# Textlens (1.0.0)

A digital text analysis dashboard, a fork based on instituutnederlandsetaal/galahad. 


## Goal

Textlens is developed as part of the CLARIAH-VL project. The goal is an application that enables digital humanties researchers to perform linguistic analysis such as tokenisation, lemmatization and part-of-speech (PoS) tagging using state-of-the art NLP tools through a user-friendly, browser-based interface, without requiring any software installation or configuration.


## Team
### Textlens development

- jonas@ccl.kuleuven.be

### Principal engineer (Galahad)

- vincent.prins@ivdnt.org

### Scientific advisors (Galahad)

- Jesse de Does
- Katrien Depuydt



## Quick start

Do you have docker and docker-compose?  Then you can clone this repository and run

```
./build.sh
docker-compose up
```
This requires an external taggers network to exists. You can use the `docker-compose.yml` from `https://github.com/instituutnederlandsetaal/taggers-dockerized` to start a taggers network.

When running Textlens locally, the webclient is available on port 8080.

Use the `docker-compose.yml` from `https://github.com/CCL-KULeuven/taggers-dockerized` to start some taggers.

### Resource limits

Resources limits (as part of the deploy keys in docker-compose files) are enabled on swarm mode. To enable them outside swarm mode, run

```
docker-compose --compatibility up
docker stats
```

The latter command to check the proper limits are set.

# Setup for development

Clone the code.

`git clone https://github.com/CCL-KULeuven/textlens.git`

## The client

Start the client.

`cd textlens/client`

`npm install`

`npm run dev`

## The server

The backend is largely unchanged with the exception of an extra language field and tagging status field. The plan is to merge the textlens and galahad backends in future releases - this serves to facilitate maintenance and future integrations with new developments from Galahad. 

Go to `http://localhost:8080/` in the browser to check the client development server is running.

Go to your favourite IDE and open the Gradle project in `textlens/server`. ... maybe some installation steps ... 

Run `textlens/server/src/main/kotlin/org/ivdnt/galahad/app/GalahadApplication.kt` from your IDE. Check `http://localhost:8010` to see whether see server is running.

Go back to the client in the browser and try to create a corpus an upload some documents.

## The taggers

In development the application will talk to the taggers through a port-forward. The port-forwards are defined in `docker-compose.yml` from `https://github.com/CCL-KULeuven/taggers-dockerized`. The port-forwards should be defined accordingly as `devport` in the taggers specifications at `server/data/taggers/*.yaml` to enable communication.

### Configuring the callback adress

The taggers send results and errors back to the server through a callback address. This address is configured in `.env`. For development, you can override the callback address with a local ip. Do the following

- `hostname -I` to see a list of available local ips
- add line `CALLBACK_SERVER=http://<your-local-ip>:8010/internal/jobs` to file `env.dev`
- launch taggers with `docker-compose --env-file .env.dev up`

## Adding a new tagger

*Asssuming you have already wrapped your tagger in a Docker image, see taggers-dockerized for examples ...*

First, launch your tagger. See `https://github.com/INL/taggers-dockerized` or `https://github.com/CCL-KULeuven/taggers-dockerized` .

Now make Textlens aware of the new tagger:

Make the specification yaml available to Textlens:
 - If you are running Galahad/Textlens server from a docker container, the specification yaml should be placed on the docker volume used by the server. Find it with
```
# List the docker volumes
# the volume is likely called textlens_tagger-volume
docker volume ls

# Inpect the volume
# We are interested in the Mountpoint property
docker inspect VOLUME_NAME

# You can check the other specifications at the mountpoint and copy your specifications
```
- If you are running Textlens server otherwise e.g. from your IDE, you can add the specifications yaml directly to `server/data/taggers/`

Refresh the browser to load the new tagger.

## Adding admins

You can configure the admins account through a file `admins.txt`. Add the desired admin users one per line. To update the file (create it if it does not exists):
```
docker compose exec server sh
cd data
vi admins.txt # make your edits
```

The client should autoreload and update to the new status, but refresh client just to be sure.


## Supported file formats
Plain text, TSV, CoNLL-U, TEI, NAF, FoLia.
For more details, see the help screen on formats on the Textlens website.


### Swagger

Once you have launched the application, you can explore the public API at

`http://localhost:8010/swagger-ui.html`

### application BasePath

The INT runs the application behind a portal on a path `/textlens`. Therefore this is set as the default path for the application. Changing this basePath requires to at least rebuild the client application with a different `vite build --base=/textlens/` set.

