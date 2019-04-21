![Cover](https://i.imgur.com/alE9OWm.png)

Instagram bot written in Java. I develop this application in my free time just for fun. :)

## 🚀 Usage
1. Setup your `config.properties` file, [download here](https://github.com/jpomykala/javagram-bot/releases/download/1.0.2/config.properties)
2. Open terminal
3. Run installation: 
```bash
curl -sL https://github.com/jpomykala/javagram-bot/releases/download/1.0.2/install.sh | bash
```
4. Start Javagram: 
```bash
curl -sL https://github.com/jpomykala/javagram-bot/releases/download/1.0.2/javagram.sh | bash
```
5. Done! Like the app? :heart_eyes: [PayPal donation 💸](https://paypal.me/jakubpomykala)

## 🛠 Configuration

| property name | default | description  |
| ------------- |:-------------:| -----:|
| username| (required) | Instagram username (phone number or login)|
| password| (required)|   Instagram password |
| likesPerDay | 100 | Number of photos to like per day |
| tags | (required) | On which tags bout should like images |
| startTime | 00:00 | Start program at the hour:time (format: HH:mm) |
| endTime | 23:59 | End program at the hour:time (format: HH:mm) |
| chanceToFollow | 0.0 | 1.0 follow user every time bot likes an image, 0.0 no chance to follow |

Example property file `config.properties` [Download here](https://github.com/jpomykala/javagram-bot/releases/download/1.0.2/config.properties)
```properties
username=your_instagram_username
password=your_instagram_password
likesPerDay=100
tags=github,programming
startTime=06:00
endTime=18:00
chanceToFollow=0.05
```

## ⚡️ Need more features?
Let me know by creating a [new issue](https://github.com/jpomykala/javagram-bot/issues/new).

## Feature development
- [x] Easy run script
- [ ] Write more test (just as good practice :) )
- [ ] Remove unused requests and functions from forked code
- [ ] Add travis configuration

#### 👩‍⚖️ Legal

This code is in no way affiliated with, authorized, maintained, sponsored or endorsed by Instagram or any of its affiliates or subsidiaries. This is an independent and unofficial API. Use at your own risk.
[Source](https://github.com/brunocvcunha/instagram4j)

#### Credits
Bot uses [this Java client](https://github.com/brunocvcunha/instagram4j) as main source and it is based on this source code. 
I decided to fork this repository and fix all main issues and add featutres just for fun. 🤠 

