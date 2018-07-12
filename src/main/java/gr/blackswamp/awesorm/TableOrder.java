package gr.blackswamp.awesorm;

class TableOrder {
    boolean ascending;
    String field;

    TableOrder(String field, boolean ascending) {
        this.ascending = ascending;
        this.field = field;
    }
}
