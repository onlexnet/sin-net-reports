echo "{" > config.yaml
echo " imageUserviceWebapp: '$USERVICE_WEBAPI_IMAGE_TAG'," >> config.yaml
echo " imageWebAppTag: '$WEBAPP_IMAGE_TAG'," >> config.yaml
echo " imageWebApiTag: '$WEBAPI_IMAGE_TAG'," >> config.yaml
echo " imageReportsTag: '$REPORTS_IMAGE_TAG'," >> config.yaml
echo "}" >> config.yaml
