/*
 */
package com.abien.xray.business.store.boundary;

import com.abien.xray.business.store.control.HitsCache;
import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("stores")
@Stateless
public class StoresResource {

    @Inject
    @Any
    Instance<HitsCache> caches;

    @Context
    ResourceContext rc;

    @Path("{id}")
    public StoreResource resource(@PathParam("id") String name) {
        CacheInstance annotation = null;
        try {
            annotation = new CacheInstance(name);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
        Instance<HitsCache> cacheInstance = caches.select(annotation);
        if (cacheInstance.isUnsatisfied()) {
            return null;
        }
        final HitsCache hitCache = cacheInstance.get();
        return rc.initResource(new StoreResource(hitCache.getCache()));
    }

    @GET
    public JsonArray all() {
        Cache.Name[] values = Cache.Name.values();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Cache.Name name : values) {
            builder.add(name.name());
        }
        return builder.build();
    }
}