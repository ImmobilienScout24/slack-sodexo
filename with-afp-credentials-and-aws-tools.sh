#!/bin/bash

AFP_COMMAND="afp"
ACCOUNT="is24-guardians"

command -v aws > /dev/null 2>&1 || {
    echo "AWS CLI tools not installed, installing them now"
    sudo apt install awscli
}

command -v $AFP_COMMAND > /dev/null 2>&1 || {
    echo "afp-cli is not installed, trying to install it now"
    command -v pip > /dev/null 2>&1 || {
        echo "pip is not installed but required, trying to install it now"
        sudo apt install python-pip
    }
    sudo pip install afp-cli --upgrade
}

echo "Please log in to the amazon federation proxy now (AD credentials)"
eval $($AFP_COMMAND --export $ACCOUNT)
echo "Temporary credentials for $ACCOUNT ready"
