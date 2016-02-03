package com.khurtious.db

import scala.slick.jdbc.meta.MTable
import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.JdbcBackend.Database

trait DataInit {

  def createTables(db: Database) = {
    db.withSession { implicit session =>
      if(MTable.getTables("ENTRIES").list(session).isEmpty){
        Q.updateNA("create table ENTRIES(" +
          "ID int not null auto_increment primary key ," +
          "SHORTURL varchar(10) ," +
          "LONGURL varchar(50)," +
          "BINDEX int)").execute
      }
    }
  }
}
