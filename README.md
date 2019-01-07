# DAAGER 

![](ui/public/logo.png)

### About

DAAGER is software build primarily as a tool for monitoring 
distributed agent-based computational platform AgE 3 (https://gitlab.com/age-agh/age3).
DAAGER provides insight into many infrastructure metrics such as system load, memory, 
disk usage, network etc. and enables user to store and view logs from all nodes that
consist of AgE 3 computational cluster. DAAGER is capable also of showing current cluster topology
with information about services that run on each node.

### Authors:
- Bartosz Radzyński
- Małgorzata Stachoń
- Jan Sznajd
- Mateusz Najdek

### Requirements:
- sbt
- npm
- docker
- docker-compose

### Running project:
Runing DAAGER is as simple as typing 
```bash
sbt dockerComposeUp
```
in terminal inside cloned repository. This will trigger DAAGER build, create 
docker image containing DAAGER, collect necessary dependencies and run them.
After successfully running command above DAAGER UI should be available at:
 ```
 localhost:9000
 ```

If Grafana prompts you to input login and password simply write `daager` / `daager`
(you can change that later).

In order to stop DAAGER simply type  
```bash
sbt dockerComposeStop
```
