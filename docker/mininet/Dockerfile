FROM ubuntu:18.04
USER root
WORKDIR /root/mininet
COPY entrypoint.sh /
RUN apt update && apt install -y --no-install-recommends \
    mininet \
    openvswitch-switch \
    iproute2 \
    iputils-ping \
    dnsutils \
    tcpdump \
    x11-xserver-utils \
    xterm
RUN DEBIAN_FRONTEND=noninteractive apt install -y --no-install-recommends wireshark
RUN rm -rf /var/lib/apt/lists/*
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
EXPOSE 6640