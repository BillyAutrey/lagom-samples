# Understanding the Lagom Client
At a high level, menu-item is a Lagom service.  Its API is described in [MenuItemService](../menu-item-api/src/main/scala/com/example/menuitem/api/MenuItemService.scala).  The restaurant-client submodule is an Akka HTTP service, which uses [Akka Discovery Service Locator](https://www.lagomframework.com/documentation/1.5.x/scala/AkkaDiscoveryIntegration.html) to find an instance of a running MenuItem service.

## Basic requirements

1. The Lagom Service descriptor's `name` must match your kubernetes service name.
2. The Kubernetes service's port must be named `http`, for your Lagom service.
3. Your client must include `lagomScaladslClient` and `"com.lightbend.lagom" %% "lagom-scaladsl-akka-discovery-service-locator" % LagomVersion.current` in its `libraryDependencies`, in [build.sbt](../build.sbt).
4. Your client code must include a concrete class that implements `LagomClientFactory`, and mix in `AkkaDiscoveryComponents`.
5. Your client and Lagom service must be in the same namespace.

If all of these requirements are met, you can request a client instance, and Lagom's Akka Discovery Service Locator will find the service, without any additional configuration.  If you follow different conventions, you can override the service locator's default behavior with additional configuration.

## Details

### MenuItem Lagom Service
The Lagom service is implemented in the [menu-item-impl](../menu-item-impl) submodule, which implements the [menu-item-api](../menu-item-api).  This service exposes a few endpoints, which are described in the [API descriptor](../menu-item-api/src/main/scala/com/example/menuitem/api/MenuItemService.scala#L33-44)

```scala
  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("menu-item-svc")
      .withCalls(
        pathCall("/api/menuItem/:id", menuItem _),
        pathCall("/api/menuItemShort/:id", menuItemShort _),
        pathCall("/api/createMenuItem", createMenuItem() )
      )
      .withAutoAcl(true)
    // @formatter:on
  }
```

The implementation, in short, handles the creation and retrieval of MenuItem objects.  It also handles serialization to/from JSON.  These are standard features in Lagom.

### Restaurant Client
The restaurant client has three important pieces.  The Lagom Client, Service API, and kubernetes service descriptors all work together to make client configuration very simple.

#### Loader.scala
This loader starts the actor system, and other key components.

It starts Akka Management, which exposes endpoints at <host>:8558/ready and <host>:8558/alive
```scala
AkkaManagement(system).start()
```

It also starts listening on specific routes, which are defined in [MenuItemRoutes.scala](#routesmenuitemroutesscala).

```scala
  val serverBinding: Future[Http.ServerBinding] =
    Http().bindAndHandle(menuItemRoutes, host, port = 8080)
```

The client is created by invoking an instance of a LagomClientFactory, and passing in the type of the API you want to contact.

```scala
  val clientFactory = new RestaurantLagomClientFactory(system, materializer)
  val menuItemClient = clientFactory.serviceClient.implement[MenuItemService]
```

#### RestaurantLagomClientFactory.scala
This is a simple class.  It provides a concrete instance to implement LagomClientFactory, and mix in the proper Service Locator.

```scala
class RestaurantLagomClientFactory(val actorSystem: ActorSystem, val materializer: Materializer)
  extends LagomClientFactory("my-client", classOf[RestaurantLagomClientFactory].getClassLoader)
    with AkkaDiscoveryComponents
    with AhcWSComponents
```

#### routes/MenuItemRoutes.scala
This route definition actually invokes the client that we instantiate as menuItemClient.  Invoking a Lagom endpoint looks like this:

```scala
menuItemClient.menuItem(id.toString).invoke()
```

If you wish to pass in a POST parameter, as described in the `Request` portion of your `ServiceCall`, you can pass it in as a parameter for the `invoke` method.

```scala
menuItemClient.createMenuItem().invoke(MenuItem(name, description, price))
``` 