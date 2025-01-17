package io.quarkus.qe.hibernate;

import io.quarkus.test.bootstrap.PostgresqlService;
import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.Container;
import io.quarkus.test.services.QuarkusApplication;

@QuarkusScenario
public class ProdHibernateIT extends BaseHibernateIT {

    static final int POSTGRESQL_PORT = 5432;

    @Container(image = "${postgresql.latest.image}", port = POSTGRESQL_PORT, expectedLog = "listening on IPv4 address")
    static final PostgresqlService database = new PostgresqlService()
            //fixme https://github.com/quarkus-qe/quarkus-test-framework/issues/455
            .withProperty("POSTGRES_USER", "user")
            .withProperty("POSTGRES_PASSWORD", "user")
            .withProperty("POSTGRES_DB", "mydb");

    @QuarkusApplication
    public static final RestService app = new RestService()
            .withProperty("quarkus.datasource.username", database.getUser())
            .withProperty("quarkus.datasource.password", database.getPassword())
            .withProperty("quarkus.datasource.jdbc.url", database::getJdbcUrl);
}
