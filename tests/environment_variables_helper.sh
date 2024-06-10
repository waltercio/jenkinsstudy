#!/usr/bin/env bash

if [ -z "$AUTHORIZATION_TOKEN_USER" ]; then
  echo "The tests cannot be executed because the environment variable AUTHORIZATION_TOKEN_USER is not set"
  exit 1
fi

if [ -z "$AUTHORIZATION_TOKEN_DEV" ]; then
  echo "The tests cannot be executed because the environment variable AUTHORIZATION_TOKEN_DEV is not set"
  exit 1
fi

if [ -z "$AUTHORIZATION_TOKEN_STG" ]; then
  echo "The tests cannot be executed because the environment variable AUTHORIZATION_TOKEN_STG is not set"
  exit 1
fi

if [ -z "$AUTHORIZATION_TOKEN_PRD" ]; then
  echo "The tests cannot be executed because the environment variable AUTHORIZATION_TOKEN_PRD is not set"
  exit 1
fi

if [ -z "$AUTHORIZATION_TOKEN_EU" ]; then
  echo "The tests cannot be executed because the environment variable AUTHORIZATION_TOKEN_EU is not set"
  exit 1
fi

if [ -z "$AUTHORIZATION_TOKEN_RUH" ]; then
  echo "The tests cannot be executed because the environment variable AUTHORIZATION_TOKEN_RUH is not set"
  exit 1
fi

if [ -z "$CONTACT_USER" ]; then
  echo "The tests cannot be executed because the environment variable CONTACT_USER is not set"
  exit 1
fi

if [ -z "$CONTACT_PASS" ]; then
  echo "The tests cannot be executed because the environment variable CONTACT_PASS is not set"
  exit 1
fi

if [ -z "$AD_USER" ]; then
  echo "The tests cannot be executed because the environment variable AD_USER is not set"
  exit 1
fi

if [ -z "$AD_PASS" ]; then
  echo "The tests cannot be executed because the environment variable AD_PASS is not set"
  exit 1
fi

if [ -z "$W3_USER" ]; then
  echo "The tests cannot be executed because the environment variable W3_USER is not set"
  exit 1
fi

if [ -z "$W3_PASS" ]; then
  echo "The tests cannot be executed because the environment variable W3_PASS is not set"
  exit 1
fi

echo "All environment variables needed to run the tests are set!"