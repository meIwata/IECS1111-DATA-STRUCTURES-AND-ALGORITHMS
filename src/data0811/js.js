// 完整的測試數據
const performanceData = [
    {vertices: 10, edges: 20, simple: 0.305, fast: 0.022, ratio: 14.03},
    {vertices: 10, edges: 45, simple: 0.100, fast: 0.017, ratio: 5.90},
    {vertices: 50, edges: 100, simple: 0.389, fast: 0.029, ratio: 13.44},
    {vertices: 50, edges: 250, simple: 0.212, fast: 0.031, ratio: 6.75},
    {vertices: 50, edges: 500, simple: 0.333, fast: 0.057, ratio: 5.87},
    {vertices: 50, edges: 1000, simple: 0.632, fast: 0.114, ratio: 5.56},
    {vertices: 50, edges: 1225, simple: 0.174, fast: 0.030, ratio: 5.87},
    {vertices: 100, edges: 200, simple: 0.203, fast: 0.010, ratio: 20.54},
    {vertices: 100, edges: 500, simple: 0.284, fast: 0.015, ratio: 18.83},
    {vertices: 100, edges: 1000, simple: 0.390, fast: 0.022, ratio: 17.58},
    {vertices: 100, edges: 2000, simple: 0.604, fast: 0.035, ratio: 17.33},
    {vertices: 100, edges: 4950, simple: 1.229, fast: 0.080, ratio: 15.41},
    {vertices: 200, edges: 400, simple: 0.602, fast: 0.019, ratio: 31.27},
    {vertices: 200, edges: 1000, simple: 1.041, fast: 0.035, ratio: 29.97},
    {vertices: 200, edges: 2000, simple: 1.708, fast: 0.056, ratio: 30.25},
    {vertices: 200, edges: 4000, simple: 2.668, fast: 0.080, ratio: 33.19},
    {vertices: 200, edges: 10000, simple: 7.074, fast: 0.071, ratio: 99.99},
    {vertices: 200, edges: 19900, simple: 9.973, fast: 0.115, ratio: 86.65},
    {vertices: 500, edges: 1000, simple: 3.753, fast: 0.037, ratio: 102.24},
    {vertices: 500, edges: 2500, simple: 7.867, fast: 0.050, ratio: 157.07},
    {vertices: 500, edges: 5000, simple: 12.156, fast: 0.064, ratio: 189.56},
    {vertices: 500, edges: 10000, simple: 19.491, fast: 0.098, ratio: 198.05},
    {vertices: 500, edges: 25000, simple: 42.765, fast: 0.183, ratio: 233.42},
    {vertices: 500, edges: 50000, simple: 89.302, fast: 0.360, ratio: 247.83},
    {vertices: 1000, edges: 2000, simple: 19.468, fast: 0.074, ratio: 263.23},
    {vertices: 1000, edges: 5000, simple: 34.045, fast: 0.093, ratio: 366.74},
    {vertices: 1000, edges: 10000, simple: 46.566, fast: 0.112, ratio: 414.07},
    {vertices: 1000, edges: 20000, simple: 77.401, fast: 0.147, ratio: 526.99},
    {vertices: 1000, edges: 50000, simple: 177.857, fast: 0.394, ratio: 450.94},
    {vertices: 1000, edges: 100000, simple: 362.833, fast: 0.694, ratio: 522.81},
    {vertices: 2000, edges: 4000, simple: 77.939, fast: 0.134, ratio: 581.27},
    {vertices: 2000, edges: 10000, simple: 149.063, fast: 0.192, ratio: 777.89},
    {vertices: 2000, edges: 20000, simple: 217.234, fast: 0.235, ratio: 925.38},
    {vertices: 2000, edges: 40000, simple: 347.230, fast: 0.373, ratio: 931.54},
    {vertices: 2000, edges: 100000, simple: 852.312, fast: 0.797, ratio: 1069.68},
    {vertices: 2000, edges: 200000, simple: 3512.162, fast: 2.628, ratio: 1336.65},
    {vertices: 5000, edges: 10000, simple: 616.436, fast: 0.388, ratio: 1587.22},
    {vertices: 5000, edges: 25000, simple: 1007.816, fast: 0.457, ratio: 2204.89}
];

const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
            position: 'top',
            labels: {
                usePointStyle: true,
                padding: 20,
                font: {
                    size: 13,
                    weight: 'bold'
                }
            }
        }
    },
    scales: {
        x: {
            grid: {
                color: 'rgba(0,0,0,0.1)'
            },
            ticks: {
                font: {
                    weight: 'bold'
                }
            }
        },
        y: {
            grid: {
                color: 'rgba(0,0,0,0.1)'
            },
            ticks: {
                font: {
                    weight: 'bold'
                }
            }
        }
    }
};

const verticesGroups = [...new Set(performanceData.map(d => d.vertices))].sort((a,b) => a-b);

new Chart(document.getElementById('timeChart'), {
    type: 'line',
    data: {
        labels: performanceData.map(d => `${d.vertices}v-${d.edges}e`),
        datasets: [{
            label: '簡單版 (ms)',
            data: performanceData.map(d => d.simple),
            borderColor: '#e17055',
            backgroundColor: 'rgba(225, 112, 85, 0.1)',
            borderWidth: 3,
            fill: false,
            tension: 0.4,
            pointRadius: 5,
            pointHoverRadius: 8,
            pointBackgroundColor: '#e17055'
        }, {
            label: 'Tarjan演算法 (ms)',
            data: performanceData.map(d => d.fast),
            borderColor: '#00b894',
            backgroundColor: 'rgba(0, 184, 148, 0.1)',
            borderWidth: 3,
            fill: false,
            tension: 0.4,
            pointRadius: 5,
            pointHoverRadius: 8,
            pointBackgroundColor: '#00b894'
        }]
    },
    options: {
        ...chartOptions,
        plugins: {
            ...chartOptions.plugins,
            title: {
                display: true,
                text: '📈 執行時間對比 (對數刻度)',
                font: {
                    size: 16,
                    weight: 'bold'
                },
                padding: 20
            }
        },
        scales: {
            ...chartOptions.scales,
            x: {
                ...chartOptions.scales.x,
                title: {
                    display: true,
                    text: '測試案例 (節點數v-邊數e)',
                    font: { weight: 'bold' }
                },
                ticks: {
                    maxRotation: 45,
                    font: { size: 10 }
                }
            },
            y: {
                ...chartOptions.scales.y,
                type: 'logarithmic',
                title: {
                    display: true,
                    text: '執行時間 (ms)',
                    font: { weight: 'bold' }
                }
            }
        }
    }
});

new Chart(document.getElementById('speedRatioChart'), {
    type: 'bar',
    data: {
        labels: performanceData.map(d => `${d.vertices}節點`),
        datasets: [{
            label: '加速比',
            data: performanceData.map(d => d.ratio),
            backgroundColor: performanceData.map((d, i) => {
                if (d.vertices <= 50) return '#74b9ff';
                if (d.vertices <= 200) return '#0984e3';
                if (d.vertices <= 500) return '#a29bfe';
                if (d.vertices <= 1000) return '#6c5ce7';
                if (d.vertices <= 2000) return '#fd79a8';
                return '#e84393';
            }),
            borderRadius: 6,
            borderWidth: 2,
            borderColor: 'white'
        }]
    },
    options: {
        ...chartOptions,
        plugins: {
            ...chartOptions.plugins,
            title: {
                display: true,
                text: '⚡ 性能提升倍數',
                font: {
                    size: 16,
                    weight: 'bold'
                },
                padding: 20
            },
            legend: {
                display: false
            }
        },
        scales: {
            ...chartOptions.scales,
            x: {
                ...chartOptions.scales.x,
                title: {
                    display: true,
                    text: '測試案例',
                    font: { weight: 'bold' }
                },
                ticks: {
                    maxRotation: 45,
                    font: { size: 10 }
                }
            },
            y: {
                ...chartOptions.scales.y,
                beginAtZero: true,
                title: {
                    display: true,
                    text: '加速比 (倍)',
                    font: { weight: 'bold' }
                }
            }
        }
    }
});

new Chart(document.getElementById('scaleabilityChart'), {
    type: 'scatter',
    data: {
        datasets: [{
            label: '簡單版演算法',
            data: performanceData.map(d => ({x: d.vertices, y: d.simple})),
            backgroundColor: 'rgba(225, 112, 85, 0.7)',
            borderColor: '#e17055',
            borderWidth: 2,
            pointRadius: function(context) {
                const value = context.parsed.y;
                return Math.max(4, Math.min(12, Math.log10(value + 1) * 3));
            },
            pointHoverRadius: 12
        }, {
            label: 'Tarjan演算法',
            data: performanceData.map(d => ({x: d.vertices, y: d.fast})),
            backgroundColor: 'rgba(0, 184, 148, 0.7)',
            borderColor: '#00b894',
            borderWidth: 2,
            pointRadius: function(context) {
                const value = context.parsed.y;
                return Math.max(4, Math.min(12, Math.log10(value + 1) * 3));
            },
            pointHoverRadius: 12
        }]
    },
    options: {
        ...chartOptions,
        plugins: {
            ...chartOptions.plugins,
            title: {
                display: true,
                text: '🎯 可擴展性分析 - 節點數 vs 執行時間',
                font: {
                    size: 16,
                    weight: 'bold'
                },
                padding: 20
            }
        },
        scales: {
            x: {
                ...chartOptions.scales.x,
                type: 'linear',
                title: {
                    display: true,
                    text: '節點數',
                    font: { weight: 'bold' }
                }
            },
            y: {
                ...chartOptions.scales.y,
                type: 'logarithmic',
                title: {
                    display: true,
                    text: '執行時間 (ms, 對數刻度)',
                    font: { weight: 'bold' }
                }
            }
        }
    }
});

const densityData = performanceData.map(d => ({
    density: d.edges / (d.vertices * (d.vertices - 1) / 2) * 100,
    vertices: d.vertices,
    edges: d.edges,
    simple: d.simple,
    fast: d.fast,
    ratio: d.ratio
}));

new Chart(document.getElementById('edgeDensityChart'), {
    type: 'bubble',
    data: {
        datasets: [{
            label: '測試案例 (氣泡大小 = 加速比)',
            data: densityData.map(d => ({
                x: d.density,
                y: d.vertices,
                r: Math.max(5, Math.min(25, d.ratio / 50))
            })),
            backgroundColor: densityData.map(d => {
                if (d.ratio < 50) return 'rgba(116, 185, 255, 0.6)';
                if (d.ratio < 200) return 'rgba(108, 92, 231, 0.6)';
                if (d.ratio < 500) return 'rgba(253, 121, 168, 0.6)';
                if (d.ratio < 1000) return 'rgba(255, 118, 117, 0.6)';
                return 'rgba(255, 99, 132, 0.8)';
            }),
            borderColor: densityData.map(d => {
                if (d.ratio < 50) return '#74b9ff';
                if (d.ratio < 200) return '#6c5ce7';
                if (d.ratio < 500) return '#fd79a8';
                if (d.ratio < 1000) return '#ff7675';
                return '#e84393';
            }),
            borderWidth: 2
        }]
    },
    options: {
        ...chartOptions,
        plugins: {
            ...chartOptions.plugins,
            title: {
                display: true,
                text: '🔍 圖密度 vs 節點數 vs 加速比',
                font: {
                    size: 16,
                    weight: 'bold'
                },
                padding: 20
            },
            tooltip: {
                callbacks: {
                    label: function(context) {
                        const index = context.dataIndex;
                        const data = densityData[index];
                        return [
                            `節點數: ${data.vertices}`,
                            `邊數: ${data.edges}`,
                            `圖密度: ${data.density.toFixed(1)}%`,
                            `加速比: ${data.ratio.toFixed(1)}×`
                        ];
                    }
                }
            }
        },
        scales: {
            x: {
                ...chartOptions.scales.x,
                title: {
                    display: true,
                    text: '圖密度 (%)',
                    font: { weight: 'bold' }
                },
                min: 0,
                max: 100
            },
            y: {
                ...chartOptions.scales.y,
                title: {
                    display: true,
                    text: '節點數',
                    font: { weight: 'bold' }
                }
            }
        }
    }
});

