package pl.edu.agh.server.application.authentication.responseview

class VerificationView {

    companion object {
        fun emailVerificationSuccessHTML(): String {
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Email Verification Successful</title>
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            text-align: center;
                            margin: 20vh auto;
                        }

                        h1 {
                            font-size: 2em;
                            color: #0066cc;
                        }

                        p {
                            font-size: 1.2em;
                            color: #333;
                        }
                    </style>
                </head>
                <body>
                    <h1>Email Verification Successful</h1>
                    <p>You can now login to your application.</p>
                </body>
                </html>
            """.trimIndent()
        }

        fun emailVerificationFailureHTML(): String {
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Email Verification Failed</title>
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            text-align: center;
                            margin: 20vh auto;
                        }

                        h1 {
                            font-size: 2em;
                            color: #FF0000;
                        }

                        p {
                            font-size: 1.2em;
                            color: #333;
                        }
                    </style>
                </head>
                <body>
                    <h1>Email Verification Failed</h1>
                    <p>Email Verification token is invalid or has expired.</p>
                </body>
                </html>
            """.trimIndent()
        }

        fun passwordChangeVerificationSuccessHTML(): String {
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Password Change Successful</title>
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            text-align: center;
                            margin: 20vh auto;
                        }

                        h1 {
                            font-size: 2em;
                            color: #0066cc;
                        }

                        p {
                            font-size: 1.2em;
                            color: #333;
                        }
                    </style>
                </head>
                <body>
                    <h1>Password Change Successful</h1>
                    <p>You can now login to your application with new password.</p>
                </body>
                </html>
            """.trimIndent()
        }

        fun passwordChangeVerificationFailureTML(): String {
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Password Change Failed</title>
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            text-align: center;
                            margin: 20vh auto;
                        }

                        h1 {
                            font-size: 2em;
                            color: #FF0000;
                        }

                        p {
                            font-size: 1.2em;
                            color: #333;
                        }
                    </style>
                </head>
                <body>
                    <h1>Password Change Failed</h1>
                    <p>Password change failed, please try again later.</p>
                </body>
                </html>
            """.trimIndent()
        }
    }
}
