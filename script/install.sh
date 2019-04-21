#!/usr/bin/env bash

echo "Setting up environment"
curl -s "https://get.sdkman.io" | bash > /dev/null
source "$HOME/.sdkman/bin/sdkman-init.sh" > /dev/null
sdk install java > /dev/null
echo "Done, now you can run Javagram!";
