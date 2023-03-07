### Test de programación Android
1. Entre a la pagina web https://api.chucknorris.io. Ahí encontrará varios endpoints que le permiten obtener bromas sobre el famoso actor Chuck Norris.
2. Utilizando el siguiente endpoint https://api.chucknorris.io/jokes/random?category=dev debe hacer una app que muestre un listado de bromas de Chuck Norris. Por cada broma o item del listado debe mostrar lo siguiente:
    - Una imagen redondeada usando el parámetro “icon_url” del json que devuelve el endpoint. :white_check_mark:
    - Un texto usando el parámetro “value” del json que devuelve el endpoint. :white_check_mark:
3. El app debe cumplir con los siguientes requerimientos.
    - Cuando se abre el app debe obtener 10 bromas del endpoint y mostrarlos. :white_check_mark:
    - Si se desliza la pantalla hacia abajo se deben cargar 10 bromas más a modo de paginación. :white_check_mark:
    - El app debe trabajar de modo offline y mostrar los ítems que ya se han descargado anteriormente si no se detecta conexión a internet. :white_check_mark:
    - Si no hay conexión a internet y no hay ítems guardados debe mostrar un texto que diga “No hay bromas para mostrar” y un botón que diga “Conectar internet” y que al darle click abra la configuración de red. :white_check_mark:


#### Informacion
Por el momento la imagen que retorna el endpoint bajo el parametro de "icon_url" parece no funcionar (https://assets.chucknorris.host/img/avatar/chuck-norris.png), indica un "not found" al intentar hacer un llamado a esta. Dado esto, en el codigo si esta llamada a esta URL falla, por default carga la imagen de Chuck Norris avatar que esta dentro del proyecto como solucion alternativa a esto.

![Chuck Norris Avatar](/app/src/main/res/drawable/chuck_norris.png "San Juan Mountains")