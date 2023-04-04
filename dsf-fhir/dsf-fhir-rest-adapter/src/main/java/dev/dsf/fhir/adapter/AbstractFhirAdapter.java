package dev.dsf.fhir.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import org.hl7.fhir.r4.model.BaseResource;

import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.parser.IParser;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

public abstract class AbstractFhirAdapter<T extends BaseResource> extends AbstractAdapter
		implements MessageBodyReader<T>, MessageBodyWriter<T>
{
	private final Class<T> resourceType;
	private final Supplier<IParser> parserFactor;

	protected AbstractFhirAdapter(Class<T> resourceType, Supplier<IParser> parserFactory)
	{
		this.resourceType = resourceType;
		this.parserFactor = parserFactory;
	}

	public final Class<? extends BaseResource> getResourceType()
	{
		return resourceType;
	}

	public final String getResourceTypeName()
	{
		return getResourceType().getAnnotation(ResourceDef.class).name();
	}

	private IParser getParser(MediaType mediaType)
	{
		return getParser(mediaType, parserFactor);
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return resourceType.equals(type);
	}

	@Override
	public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException
	{
		getParser(mediaType).encodeResourceToWriter(t, new OutputStreamWriter(entityStream));
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return resourceType.equals(type);
	}

	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException
	{
		return fixResource(getParser(null).parseResource(type, new InputStreamReader(entityStream)));
	}

	protected T fixResource(T resource)
	{
		return resource;
	}
}
