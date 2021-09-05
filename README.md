# ShardMC
 Distribute players across multiple servers while syncing packets

![Two players on different server](images/sharedpackets.gif?raw=true "Title")

ShardMC aims to be a simple plugin that allows multiple servers to sync player locations and block states. This isn't intended to be used on a survival server, but a concert or building server where you want high player counts being able to see each other.

### Requirements
- A copy of the plugin on each server you want to sync
- Each server should start with the same map
- RabbitMQ running on the same host as all the servers


Inspired by [playeremulator](https://www.youtube.com/watch?v=SbJYXcTolUk) by egg82 and KennyTV