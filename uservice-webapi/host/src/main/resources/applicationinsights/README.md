Placeholder for files related to Application insights.
Alternatives:
- use BP_INCLUDE_FILES (https://paketo.io/docs/howto/java/#include-or-exclude-custom-files) I can't achieve to add extra files using such way
- add init container do townload applicationinsights jar : Terraform does not support init containers for container apps atm (https://github.com/Azure/terraform-azure-container-apps/issues/36)

Current approach:
 - add to resource folder host/src/main/resources/applicationinsights required version of applicationinsight agent using wget download in CI build
 - as we build using spring-boot:build-image (it uses packeto build behind the sceen), it will be recreated in target image together with codebase, it means default location /workspace/BOOT-INF/classes/applicationinsights
