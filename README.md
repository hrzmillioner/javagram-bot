# Javagram

Instagram bot written in Java. I develop this application in my free time just for fun. :)

## Configuration

| property name | default | description  |
| ------------- |:-------------:| -----:|
| username| (required) | Instagram username (phone number or login)|
| password| (required)|   Instagram password |
| likesPerDay | 100 | Number of photos to like per day |
| tags | (required) | On which tags bout should like images |
| startTime | 00:00 | Start program at the hour:time (format: HH:mm) |
| endTime | 23:59 | End program at the hour:time (format: HH:mm) |
| chanceToFollow | 0.0 | 1.0 follow user every time bot likes an image, 0.0 no chance to follow |

Example property file `config.properties`
```
username=your_instagram_username
password=your_instagram_password
likesPerDay=100
tags=github,programming
startTime=06:00
endTime=18:00
chanceToFollow=0.05
```

## Usage
0. [Install Java (JRE)](https://www.java.com/en/download/)
1. [Download latest release (zip file)](https://github.com/jpomykala/javagram-bot/releases)
2. Unzip 
3. Edit `config.properties`
4. Run in terminal: `java -jar javagram.jar -config config.properties`

Soon application will be working without Java installed on the local computer.

Like the app? :heart_eyes: [PayPal donation](https://paypal.me/jakubpomykala)

#### Run in background:
This works only on Raspberry/Linux/MacOs:

`nohup java -jar javagram.jar -config config.properties &> output.log&`

## Need more feature?
Let me know by creating a [new issue](https://github.com/jpomykala/javagram-bot/issues/new).

## Feature development
- Add minimal Docker image with Java installed to run the application
- Migrate to Java 11 and give the ability to run the application without Java installed
- Write more test (just as good practice :) )
- Remove unused requests and functions from forked code
- Add more configuration options
- Add travis configuration


#### Terms and conditions

- You will NOT use this API for marketing purposes (spam, botting, harassment, massive bulk messaging...).
- We do NOT give support to anyone who wants to use this API to send spam or commit other crimes.
- We reserve the right to block any user of this repository that does not meet these conditions.
[Source](https://github.com/brunocvcunha/instagram4j)

#### Legal

This code is in no way affiliated with, authorized, maintained, sponsored or endorsed by Instagram or any of its affiliates or subsidiaries. This is an independent and unofficial API. Use at your own risk.
[Source](https://github.com/brunocvcunha/instagram4j)

#### Credits
Bot uses [this Java client](https://github.com/brunocvcunha/instagram4j) as main source and it is based on this source code. 
I decided to fork this repository and fix all main issues like 

