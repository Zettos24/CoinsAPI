package de.zettos.coinsapi;

import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.UUID;

public class CoinsAPI {


    private final MySQL mySQL;
    private final String type;
    private final String tableName;

    public CoinsAPI(String type, String tableName) {
        this.mySQL = new MySQL();
        this.type = type;
        this.tableName = tableName;
    }

    public void connect(String host, int port, String database, String username, String password){
        mySQL.setCredentials(host,port,database,username,password);
        mySQL.connect(() -> {

            mySQL.createTable(this.tableName, "UUID Text, "+this.type+" LONG");
        });
    }

    public void disconnect(){
        mySQL.disconnect();
    }

    @SneakyThrows
    public void setCoins(UUID uuid, long coins) {
        if(hasCoins(uuid)){
            this.mySQL.getConnection().prepareStatement("UPDATE "+this.tableName+" SET "+this.type+" = '"+coins+"' WHERE UUID = '"+uuid.toString()+"'").executeUpdate();

        }else this.mySQL.getConnection().prepareStatement("INSERT INTO "+this.tableName+" (UUID, "+this.type+") VALUES ('"+uuid.toString()+"', '"+coins+"')").executeUpdate();
    }

    @SneakyThrows
    public void clearCoins(UUID uuid){
        if(hasCoins(uuid)){
            this.mySQL.getConnection().prepareStatement("DELETE FROM "+this.tableName+" WHERE UUID = '"+uuid.toString()+"'").executeUpdate();
        }
    }

    @SneakyThrows
    public void addCoins(UUID uuid, long coins){
        if(hasCoins(uuid)){
            long current = getCoins(uuid)+coins;
            this.mySQL.getConnection().prepareStatement("UPDATE "+this.tableName+" SET "+this.type+" = '"+current+"' WHERE UUID = '"+uuid.toString()+"'").executeUpdate();

        }else this.mySQL.getConnection().prepareStatement("INSERT INTO "+this.tableName+" (UUID, "+this.type+") VALUES ('"+uuid.toString()+"', '"+coins+"')").executeUpdate();
    }

    @SneakyThrows
    public void removeCoins(UUID uuid, long coins){
        if(hasCoins(uuid)){
            long current = getCoins(uuid)-coins;
            if(current < 0){
                current = 0;
            }
            this.mySQL.getConnection().prepareStatement("UPDATE "+this.tableName+" SET "+this.type+" = '"+current+"' WHERE UUID = '"+uuid.toString()+"'").executeUpdate();
        }
    }

    @SneakyThrows
    public boolean hasCoins(UUID uuid){
        return this.mySQL.getConnection().prepareStatement("SELECT "+this.type+" FROM "+this.tableName+" WHERE UUID = '"+uuid.toString()+"'").executeQuery().next();
    }

    @SneakyThrows
    public long getCoins(UUID uuid){
        long l = 0;
        if(hasCoins(uuid)){
            ResultSet rs = this.mySQL.getConnection().prepareStatement("SELECT "+this.type+" FROM "+this.tableName+" WHERE UUID = '"+ uuid +"'").executeQuery();
            while (rs.next()){
                l = rs.getLong("Coins");
            }
        }
        return l;
    }

    public String getType() {
        return type;
    }

    public String getTableName() {
        return tableName;
    }

}
