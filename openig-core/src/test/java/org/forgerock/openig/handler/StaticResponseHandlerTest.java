/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2010-2011 ApexIdentity Inc.
 * Portions Copyright 2011-2015 ForgeRock AS.
 */

package org.forgerock.openig.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.forgerock.http.protocol.Response;
import org.forgerock.http.protocol.Status;
import org.forgerock.openig.el.Expression;
import org.forgerock.services.context.AttributesContext;
import org.forgerock.services.context.RootContext;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class StaticResponseHandlerTest {

    @Test
    public void shouldSetStatusReasonAndHeaders() throws Exception {
        final StaticResponseHandler handler = new StaticResponseHandler(Status.FOUND);
        handler.addHeader("Location", Expression.valueOf("http://www.example.com/", String.class));
        Response response = handler.handle(new RootContext(), null).get();
        assertThat(response.getStatus()).isEqualTo(Status.FOUND);
        assertThat(response.getHeaders().getFirst("Location")).isEqualTo("http://www.example.com/");
    }

    @Test
    public void shouldEvaluateTheEntityExpressionContent() throws Exception {
        Expression<String> expression =
                Expression.valueOf("<a href='/login?goto=${urlEncode(attributes.goto)}'>GOTO</a>",
                                   String.class);
        final StaticResponseHandler handler = new StaticResponseHandler(Status.OK, null, expression);

        final AttributesContext context = new AttributesContext(new RootContext());
        context.getAttributes().put("goto", "http://goto.url");
        Response response = handler.handle(context, null).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getEntity().getString()).isEqualTo(
                "<a href='/login?goto=http%3A%2F%2Fgoto.url'>GOTO</a>");
    }
}
