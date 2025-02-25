<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%-- JSP TAGS --%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="my" tagdir="/WEB-INF/tags"%>

<%-- CONTENT --%>
<my:page>

    <jsp:attribute name="script">
        <script type="text/javascript" src="${lib}/jquery.spring-friendly/jquery.spring-friendly.js"></script>
    </jsp:attribute>
    <jsp:attribute name="pageHeaderTitle">RIM Database</jsp:attribute>

    <jsp:body>
        <br/>
        <div class="aca-data-table">
            <table id="digestValueTable" class="display" width="100%">
                <thead>
                    <tr>
                        <th>Manufacturer</th>
                        <th>Model</th>
                        <th>Event Type</th>
                        <th>PCR Index</th>
                        <th>Digest Value</th>
                        <th>Base RIM</th>
                        <th>Support RIM</th>
                    </tr>
                </thead>
            </table>
        </div>

        <script>
            $(document).ready(function() {
                let url = pagePath +'/list';
                let columns = [
                        {data: 'manufacturer',
                            orderable: true,
                            searchable:false},
                        {data: 'model',
                            orderable: false,
                            searchable:false},
                        {data: 'eventType',
                            orderable: false,
                            searchable:false,},
                        {data: 'pcrIndex',
                            orderable: true,
                            searchable:false},
                        {data: 'digestValue',
                            orderable: false,
                            searchable:false},
                        {data: 'baseRimId',
                            orderable: false,
                            searchable: false,
                            render: function(data, type, full, meta) {
                                return rimDetailsLink(full.baseRimId);
                            }
                        },
                        {data: 'supportRimId',
                            orderable: false,
                            searchable: false,
                            render: function(data, type, full, meta) {
                                return rimDetailsLink(full.supportRimId);
                            }
                        }
                    ];

                //Set data tables
                setDataTables("#digestValueTable", url, columns);
            });
        </script>
    </jsp:body>
</my:page>