var nodes = new vis.DataSet([]);
var edges = new vis.DataSet([]);
var container = document.getElementById('kubegraph');

var data = {
    nodes: nodes,
    edges: edges
};

var options = {
    autoResize: true,
    interaction: {
        tooltipDelay: 120,
        hover: true,
        navigationButtons: true
    },    
    layout: {
        improvedLayout: true,
    },
    groups: {
        node: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf233',
                size: 50,
                color: '#3366ff'
            }
        },
        replicationcontroller: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf0c5',
                size: 50,
                color: '#3366ff'
            }
        },
        deployment: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf085',
                size: 50,
                color: '#3366ff'
            }
        },
        pod: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf1b2',
                size: 50,
                color: '#3366ff'
            }
        },
        replicaset: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf013',
                size: 50,
                color: '#3366ff'
            }
        },
        service: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf0c2',
                size: 50,
                color: '#3366ff'
            }
        },
    }
};

var network = new vis.Network(container, data, options);

