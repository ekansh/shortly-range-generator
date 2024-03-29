# shortly-range-generator
The purpose of this service is to pre-generate range of alias that the controller serving the create alias and resolve alias can use. This logic can be put in the shortly service and executed during bootstrap time. However, if there is complex logic in pre-generating ranges then time to spin up EC2 will increase.

Full story [here](https://medium.com/@mail.ekansh/shortly-aws-implementation-d76bff78550e)

**Note**:: the purpose of this code base was to prototype and not to create production ready code.

## External dependency to this application 
Database : shortly , schema : public, table: ranges

```

CREATE DATABASE shortly;
-- public schema should exist by default, if not then : 
CREATE SCHEMA public;

CREATE TABLE public.ranges (
    id BIGSERIAL NOT NULL,
    ec2_id VARCHAR(10),
    start_range VARCHAR(8),
    end_range VARCHAR(8),
    status VARCHAR(10)
);
```

 - URL: shortly.cqrni4n4j4dx.us-east-2.rds.amazonaws.com:5432
 - user: root
 - password:
 - database: shortly

## Testing the application locally 
Make sure the shortly db has: 
1. Make sure database is Publicly accessible. This will assign the IP address to the DB. 
2. It will have a security group RDS_SG , make sure it has inbound security rule which allows traffic from your IP. Since it is for testing purpose you can allow all traffic, from all port, all protocol from "My IP". 
3. Go to `BasicUtils.java` and uncomment `getEnvVariable` code base which allows setting the local variable. 
4. Run `RangeGeneratorTest`
Make sure to undo secruity steps 1,2 from the above steps to keep the database secured

## Building the application
Apache Maven 3.8.6
Java version: 17.0.5, vendor: Oracle Corporation
`mvn clean package`
## Deploying the application on AWS
Deploy the `shortly-range-generator\target\hash-range.jar` in `jvmlam` Lambda. Ensure the shortly db is up and Lambda can access the DB. That is secruity group attached to the DB (RDS_SG) with in-bound rule that allow connections from a security group associated with Lambda (lambdasg). Most likely this security group RDS_SG is already associated with DB. Also lambda needs to belong to the VPC in which your DB sits.

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

