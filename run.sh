docker network create elastic
docker run \
	--name es01 \
	--net elastic \
	-p 9200:9200 \
	-p 9300:9300 \
	-e "discovery.type=single-node" \
	-e ELASTIC_PASSWORD=elastic
	-e ES_JAVA_OPTS="-Xms1g -Xmx1g" \
	-it \
	docker.elastic.co/elasticsearch/elasticsearch:8.4.1


docker cp es01:/usr/share/elasticsearch/config/certs/http_ca.crt .


docker run \
	--name kib-01 \
	--net elastic \
	-p 5601:5601 \
	docker.elastic.co/kibana/kibana:8.4.1



# Get new enrollment token
docker exec -it es01 \
    /usr/share/elasticsearch/bin/elasticsearch-create-enrollment-token \
    -s kibana