#!/bin/bash

ps -eo pid --sort=start_time | tail -n 1
