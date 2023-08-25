package principal;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import productos.Almacen;
import productos.Producto;

import java.io.*;
import java.util.ArrayList;


public class Principal extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1200);

        Almacen almacen_principal = new Almacen();

        VBox stock_stack = new VBox();
        stock_stack.setSpacing(50);



        TableView<Producto> tabla = new TableView<>();

        TableColumn<Producto, Integer> codigoColumn = new TableColumn<>("Código");
        TableColumn<Producto, String> descripcionColumn = new TableColumn<>("Descripción");
        TableColumn<Producto, Integer> precioColumn = new TableColumn<>("Precio Unitario");
        TableColumn<Producto, Integer> stockActualColumn = new TableColumn<>("Stock Actual");
        TableColumn<Producto, Integer> stockMinimoColumn = new TableColumn<>("Stock Mínimo");
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));codigoColumn.setPrefWidth(50);
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));descripcionColumn.setPrefWidth(200);
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio_unitario"));precioColumn.setPrefWidth(100);
        stockActualColumn.setCellValueFactory(new PropertyValueFactory<>("stock_actual"));stockActualColumn.setPrefWidth(100);
        stockMinimoColumn.setCellValueFactory(new PropertyValueFactory<>("stock_minimo"));stockMinimoColumn.setPrefWidth(100);

        tabla.getColumns().addAll(codigoColumn, descripcionColumn, precioColumn, stockActualColumn, stockMinimoColumn);



        HBox formularios = new HBox();
        formularios.getStyleClass().add("formulario");
        formularios.setSpacing(30);

        TextField codigo = new TextField();
        codigo.setPromptText("Codigo de producto");

        TextField descripcion = new TextField();
        descripcion.setPromptText("Descripcion");

        TextField precio_unitario = new TextField();
        precio_unitario.setPromptText("Precio Unitario");

        TextField stock_actual = new TextField();
        stock_actual.setPromptText("Stock actual");

        TextField stock_minimo = new TextField();
        stock_minimo.setPromptText("Stock minimo");

        Button boton_agregar = new Button("Agregar Producto");
        boton_agregar.setOnAction(actionEvent -> {
            almacen_principal.agregarProducto(Integer.parseInt(codigo.getText()),descripcion.getText(),Integer.parseInt(precio_unitario.getText()),Integer.parseInt(stock_actual.getText()),Integer.parseInt(stock_minimo.getText()));
            codigo.clear();
            descripcion.clear();
            precio_unitario.clear();
            stock_actual.clear();
            stock_minimo.clear();
        });
        Button boton_imprimir = new Button("Imprimir Productos");
        boton_imprimir.setOnAction(actionEvent -> {
            ArrayList<Producto> listado_productos = almacen_principal.listarProductos();
            for (Producto p : listado_productos){
                System.out.println("Codigo " + p.getCodigo() + " Descripcion:" + p.getDescripcion() + " Precio Unitario: " + p.getPrecio_unitario() + " Stock Actual: " + p.getStock_actual() + " Stock minimo: " + p.getStock_minimo());
            }
            ObservableList<Producto> productosObservableList = FXCollections.observableArrayList(listado_productos);
            tabla.setItems(productosObservableList);

        });

        formularios.getChildren().addAll(codigo,descripcion,precio_unitario,stock_actual,stock_minimo,boton_agregar,boton_imprimir);

        stock_stack.getChildren().addAll(formularios,tabla);

        Scene stock_scene = new Scene(stock_stack,1200,800);




        // Escena del menu
        HBox hbox = new HBox();

        StackPane panel1 = new StackPane();
        panel1.getStyleClass().add("panel1");
        panel1.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.40));
        panel1.prefHeightProperty().bind(primaryStage.heightProperty());

        StackPane panel2 = new StackPane();
        panel2.getStyleClass().add("panel2");
        panel2.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.60));
        panel2.prefHeightProperty().bind(primaryStage.heightProperty());

        VBox botones = new VBox();
        botones.setSpacing(90);

        Button boton_venta = new Button("Nueva venta");
        boton_venta.getStyleClass().add("boton");
        boton_venta.prefWidthProperty().bind(panel2.widthProperty().multiply(0.5));
        boton_venta.prefHeightProperty().bind(panel2.heightProperty().multiply(0.15));


        Button boton_stock = new Button("Administrar Stock");
        boton_stock.getStyleClass().add("boton");
        boton_stock.prefWidthProperty().bind(panel2.widthProperty().multiply(0.5));
        boton_stock.prefHeightProperty().bind(panel2.heightProperty().multiply(0.15));
        boton_stock.setOnAction(actionEvent -> {
            primaryStage.setScene(stock_scene);
        });

        Button boton_historial = new Button("Ver historial");
        boton_historial.getStyleClass().add("boton");
        boton_historial.prefWidthProperty().bind(panel2.widthProperty().multiply(0.5));
        boton_historial.prefHeightProperty().bind(panel2.heightProperty().multiply(0.15));

        botones.getChildren().addAll(boton_venta,boton_stock,boton_historial);
        botones.setAlignment(Pos.CENTER);
        panel2.getChildren().add(botones);


        hbox.getChildren().addAll(panel1, panel2);

        Scene scene = new Scene(hbox, 1200, 800);
        scene.getStylesheets().add("stylesheet.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Administrador");
        primaryStage.show();



    }


}
