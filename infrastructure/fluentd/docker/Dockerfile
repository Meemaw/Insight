FROM fluent/fluentd:v1.10-1

USER root
RUN apk add --no-cache --update --virtual .build-deps \
        build-base ruby-dev \
        && gem install fluent-plugin-elasticsearch fluent-plugin-input-gelf \
        && gem sources --clear-all \
        && apk del .build-deps \
        && rm -rf /tmp/* /var/tmp/* /usr/lib/ruby/gems/*/cache/*.gem

ARG fluent_conf=conf/fluent.conf
COPY $fluent_conf /fluentd/etc/fluent.conf

USER fluent
