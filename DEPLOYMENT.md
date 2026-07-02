# Deployment polocy

- Each change in **main** branch starts deployment to prd01 environment. Why directly to prod? The project is maintained by one person, so is simpler to push to prod and fix / patch instead of maintain a few environments where only prd01 is used. Current frontend entry point is https://time.onlex.net.
