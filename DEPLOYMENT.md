# Deployment polocy

- Each change in **main** branch starts deployment to prd01 environment. Why directly to prod? The project is maintained by one person, so is simpler to push to prod and fix / patch instead of main tain a few environments, where only prd01 is used. Entry point of the apllication is is https://sinnet.onlex.net

- each change (e.g. force push) to branch origin/webapp-test deploys frontend part (static react app) to alternative location https://sinnet-test.onlex.net. It allows to create new UI and customers can test it before final release.
