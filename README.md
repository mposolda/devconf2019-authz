# devconf-2019-authz
Keycloak/Authorization SpringBoot example for devconf 2019

# How to have it running

1) Start Keycloak:
```
cd $KEYCLOAK_HOME/bin
./standalone.sh -Djboss.socket.binding.port-offset=100
```

2) Import realm cars-realm.json

3) Build
```
    mvn clean install
```    

4) Run CarsServiceApp and CarsApp from IDE (TODO: Need to describe how to run from mvn with the springboot plugin)

5) Go to http://localhost:8080/app and login as alice/alice or other user (See cars-realm.json for the users/passwords).

6) Switching between branches `master` for not-UMA setup (demo part1) and `authz-backup` for UMA setup (demo part2)



