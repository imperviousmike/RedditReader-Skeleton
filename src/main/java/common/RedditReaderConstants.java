/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author mike
 */
public enum RedditReaderConstants {

    IMAGE_LOGIC("Image"), ACCOUNT_LOGIC("Account"), BOARD_LOGIC("Board"),HOST_LOGIC("Host"), BOARD("Wallpaper");

    private String field;

    private RedditReaderConstants(String field) {
        this.setField(field);
    }

    private String getField() {
        return field;
    }

    private void setField(String field) {
        this.field = field;
    }

    public String toString() {
        return getField();
    }

}
