# Set the default label
: ${VERSION_LABEL:=dev}

./scripts/build-all.sh

echo "Will push textlens images with version <$VERSION_LABEL>. Set VERSION_LABEL to override this."

docker push cclkuleuven/textlens-proxy:$VERSION_LABEL
docker push cclkuleuven/textlens-server:$VERSION_LABEL
docker push cclkuleuven/textlens-client:$VERSION_LABEL
