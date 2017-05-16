FROM fluent/fluentd:latest
MAINTAINER your_name <...>
USER root
WORKDIR /home/fluent
ENV PATH /home/fluent/.gem/ruby/2.2.0/bin:$PATH
RUN gem install fluent-plugin-secure-forward fluent-plugin-elasticsearch fluent-plugin-burrow fluent-plugin-grep
EXPOSE 24284
ADD fluent.conf /fluentd/etc/$FLUENTD_CONF
CMD fluentd -c /fluentd/etc/$FLUENTD_CONF -p /fluentd/plugins $FLUENTD_OPT