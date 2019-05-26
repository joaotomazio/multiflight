FROM openjdk:9
USER root
WORKDIR /root/floodlight
RUN apt update && apt install -y --no-install-recommends ant
RUN rm -rf /var/lib/apt/lists/*
EXPOSE 6653 8080
ENTRYPOINT ["bash"]
