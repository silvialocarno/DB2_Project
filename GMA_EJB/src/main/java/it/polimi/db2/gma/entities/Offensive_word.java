package it.polimi.db2.gma.entities;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

/**
 * The persistent class for the expenses database table.
 *
 */

@Entity
@Table(name = "offensive_word", schema = "db_gamified_marketing_application")

public class Offensive_word implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int offensive_word_id;

    private String word;

    public int getOffensive_word_id() {
        return offensive_word_id;
    }

    public void setOffensive_word_id(int offensive_word_id) {
        this.offensive_word_id = offensive_word_id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }



}