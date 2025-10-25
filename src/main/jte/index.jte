<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <title>Steamosaic</title>
    <style>
        *, *:before, *:after {
            box-sizing: border-box;
            margin: 0;
        }

        html, body, main {
            width: 100%;
            height: 100%;
        }

        body {
            font-family: system-ui, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }

        main {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            gap: 2rem;
        }

        form {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            gap: 1rem;
        }

        input {
            padding: 0.5rem;
        }
    </style>
</head>
<body>
<main>
    <h1>Steamosaic</h1>
    <form action="/api/mosaic" method="post" enctype="multipart/form-data">
        <label for="identifier">Enter your Steam username or ID.</label>
        <input type="text" id="identifier" name="identifier" required placeholder="kisats or 76561198842603734">
        <button type="submit">Continue</button>
    </form>
    <p><a href="https://github.com/seaneoo/steamosaic" target="_blank">Source code</a></p>
</main>
</body>
</html>
