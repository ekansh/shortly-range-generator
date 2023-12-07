# shortly-range-generator

##External dependency to this application 
Database: URL/ user/ password / db name/ db type

## Testing the application locally 
Make sure the shortly db has: 
1. Make sure database is Publicly accessible. This will assign the IP address to the DB. 
2. It will have a security group RDS_SG , make sure it has inbound security rule which allows traffic from your IP. Since it is for testing purpose you can allow all traffic, from all port, all protocol from "My IP". 
3. Go to `BasicUtils.java` and uncomment `getEnvVariable` code base which allows setting the local variable. 
4. Run `RangeGeneratorTest`
Make sure to undo secruity steps 1,2 from the above steps to keep the database secured

## Building the application
`mvn clean package`
## Deploying the application on AWS
Deploy the `shortly-range-generator\target\hash-range.jar` in `jvmlam` Lambda.

## Testing the application on AWS
Visit the `Function URL`: `https://kamnerad33voeotao5bkp6i4yi0yoxat.lambda-url.us-east-2.on.aws/` . An output similar to this should show up : 
```
{
    "range_end": "akaaa2K",
    "range_start": "akaaa23",
    "range_list": "akaaa23,akaaa24,akaaa25,akaaa26,akaaa27,akaaa28,akaaa29,akaaa2+,akaaa2=,akaaa2A,akaaa2B,akaaa2C,akaaa2D,akaaa2E,akaaa2F,akaaa2G,akaaa2H,akaaa2I,akaaa2J,akaaa2K"
}
```
Also visit CloudWatch\Log groups: `/aws/lambda/jvmlam`  and ensure logs are clean . 

