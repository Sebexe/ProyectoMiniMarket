package principal;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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

        Almacen almacen_principal = cargarAlmacen();
        ObservableList<Producto> productosObservableList = FXCollections.observableArrayList(almacen_principal.listarProductos());

        VBox stock_stack = new VBox();
        stock_stack.setSpacing(50);



        TableView<Producto> tabla = new TableView<>();

        TableColumn<Producto, Integer> codigoColumn = new TableColumn<>("Código");
        TableColumn<Producto, String> descripcionColumn = new TableColumn<>("Descripción");
        TableColumn<Producto, Integer> precioColumn = new TableColumn<>("Precio Unitario");
        TableColumn<Producto, Integer> stockActualColumn = new TableColumn<>("Stock Actual");
        TableColumn<Producto, Integer> stockMinimoColumn = new TableColumn<>("Stock Mínimo");
        TableColumn<Producto, String> estadoColumn = new TableColumn<>("Estado");
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));codigoColumn.setPrefWidth(50);
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));descripcionColumn.setPrefWidth(200);
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio_unitario"));precioColumn.setPrefWidth(100);
        stockActualColumn.setCellValueFactory(new PropertyValueFactory<>("stock_actual"));stockActualColumn.setPrefWidth(100);
        stockMinimoColumn.setCellValueFactory(new PropertyValueFactory<>("stock_minimo"));stockMinimoColumn.setPrefWidth(100);
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));


        tabla.getColumns().addAll(codigoColumn, descripcionColumn, precioColumn, stockActualColumn, stockMinimoColumn,estadoColumn);
        tabla.setItems(productosObservableList);


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

        HBox formularios_modificar = new HBox();
        formularios_modificar.getStyleClass().add("formulario");
        formularios_modificar.setSpacing(30);

        TextField codigo_modificar = new TextField();
        codigo_modificar.setPromptText("Codigo de producto");

        TextField existencias_agregar = new TextField();
        existencias_agregar.setPromptText("Existencias nuevas");
        Button boton_agregar_existencias = new Button("Agregar existencias");
        boton_agregar_existencias.setOnAction(actionEvent -> {
            int codigo_producto = Integer.parseInt(codigo_modificar.getText());
            if (almacen_principal.estaProducto(codigo_producto)){
                int nuevo_ingreso = Integer.parseInt(existencias_agregar.getText());
                almacen_principal.agregarExistencias(codigo_producto,nuevo_ingreso);
                codigo_modificar.clear();
                existencias_agregar.clear();
                almacen_principal.calcularExistencias(codigo_producto);
                guardarAlmacen(almacen_principal);
                productosObservableList.setAll(almacen_principal.listarProductos());
            }
            else {
                System.out.println("El producto no esta en el almacen.");
            }
        });
        Button boton_sacar_existencias = new Button("Sacar existencias");
        boton_sacar_existencias.setOnAction(actionEvent -> {
            int codigo_producto = Integer.parseInt(codigo_modificar.getText());
            if (almacen_principal.estaProducto(codigo_producto)){
                int nuevo_ingreso = Integer.parseInt(existencias_agregar.getText());
                almacen_principal.sacarExistencias(codigo_producto,nuevo_ingreso);
                codigo_modificar.clear();
                existencias_agregar.clear();
                almacen_principal.calcularExistencias(codigo_producto);
                guardarAlmacen(almacen_principal);
                productosObservableList.setAll(almacen_principal.listarProductos());
            }
            else {
                System.out.println("El producto no esta en el almacen.");
            }
        });
        Button boton_eliminar_producto = new Button("Eliminar Producto");
        boton_eliminar_producto.setOnAction(actionEvent -> {
            int codigo_producto = Integer.parseInt(codigo_modificar.getText());
            if (almacen_principal.estaProducto(codigo_producto)){
                almacen_principal.sacarProducto(codigo_producto);
                guardarAlmacen(almacen_principal);
                productosObservableList.setAll(almacen_principal.listarProductos());
            }
            else {
                System.out.println("El producto no esta en el almacen.");
            }
        });

        formularios_modificar.getChildren().addAll(codigo_modificar,existencias_agregar,boton_agregar_existencias,boton_sacar_existencias,boton_eliminar_producto);



        Button boton_agregar = new Button("Agregar Producto");
        boton_agregar.setOnAction(actionEvent -> {
            almacen_principal.agregarProducto(Integer.parseInt(codigo.getText()),descripcion.getText(),Integer.parseInt(precio_unitario.getText()),Integer.parseInt(stock_actual.getText()),Integer.parseInt(stock_minimo.getText()));
            almacen_principal.calcularExistencias(Integer.parseInt(codigo.getText()));
            codigo.clear();
            descripcion.clear();
            precio_unitario.clear();
            stock_actual.clear();
            stock_minimo.clear();
            guardarAlmacen(almacen_principal);
            productosObservableList.setAll(almacen_principal.listarProductos());
        });
        Button boton_imprimir = new Button("Volver al Menu");


        formularios.getChildren().addAll(codigo,descripcion,precio_unitario,stock_actual,stock_minimo,boton_agregar,boton_imprimir);

        stock_stack.getChildren().addAll(formularios,formularios_modificar,tabla);

        Scene stock_scene = new Scene(stock_stack,1200,800);




        // MENU

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

        boton_imprimir.setOnAction(actionEvent -> {
            primaryStage.setScene(scene);
        });



    }

    private Almacen cargarAlmacen() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("datos.dat"))) {
            return (Almacen) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new Almacen(); // Si ocurre un error, devuelve una lista vacía
        }
    }

    private void guardarAlmacen(Almacen almacen) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("datos.dat"))) {
            outputStream.writeObject(almacen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
