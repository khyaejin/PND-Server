import * as d3 from 'd3';
import * as type from './type';

const OTHER_NAME = 'other';
const OTHER_COLOR = '#444444';

export const createPieLanguage = (
    svg: d3.Selection<SVGSVGElement, unknown, null, unknown>,
    repositoryInfo: type.RepositoryInfo, // userInfo 대신 repositoryInfo 사용
    x: number,
    y: number,
    width: number,
    height: number,
    settings: type.PieLangSettings,
    isForcedAnimation: boolean
): void => {
    if (repositoryInfo.contributions.length === 0) {
        return;
    }

    // 상위 5개의 언어 선택
    const languages = repositoryInfo.languages.slice(0, 5);

    // 선택된 언어들의 기여도 합산
    const sumContrib = languages
        .map((lang) => lang.contributions)
        .reduce((a, b) => a + b, 0);

    // 기타 언어의 기여도 계산
    const otherContributions = repositoryInfo.totalCommitContributions - sumContrib;
    if (otherContributions > 0) {
        languages.push({
            language: OTHER_NAME,
            color: OTHER_COLOR,
            contributions: otherContributions,
        });
    }

    const isAnimate = settings.growingAnimation || isForcedAnimation;
    const animeSteps = 5;
    const animateOpacity = (num: number) =>
        Array<string>(languages.length + animeSteps)
            .fill('')
            .map((d, i) => (i < num ? 0 : Math.min((i - num) / animeSteps, 1)))
            .join(';');

    const radius = height / 2;
    const margin = radius / 10;

    const row = 8;
    const offset = (row - languages.length) / 2 + 0.5;
    const fontSize = height / row / 1.5;

    const pie = d3
        .pie<type.LangInfo>()
        .value((d) => d.contributions)
        .sortValues(null);
    const pieData = pie(languages);

    const group = svg.append('g').attr('transform', `translate(${x}, ${y})`);

    const groupLabel = group
        .append('g')
        .attr('transform', `translate(${radius * 2.1}, ${0})`);

    // labels에 대한 markers
    const markers = groupLabel
        .selectAll(null)
        .data(pieData)
        .enter()
        .append('rect')
        .attr('x', 0)
        .attr('y', (d) => (d.index + offset) * (height / row) - fontSize / 2)
        .attr('width', fontSize)
        .attr('height', fontSize)
        .attr('fill', (d) => d.data.color)
        .attr('stroke', settings.backgroundColor)
        .attr('stroke-width', '1px');
    if (isAnimate) {
        markers
            .append('animate')
            .attr('attributeName', 'fill-opacity')
            .attr('values', (d, i) => animateOpacity(i))
            .attr('dur', '3s')
            .attr('repeatCount', '1');
    }

    // labels
    const labels = groupLabel
        .selectAll(null)
        .data(pieData)
        .enter()
        .append('text')
        .attr('dominant-baseline', 'middle')
        .text((d) => d.data.language)
        .attr('x', fontSize * 1.2)
        .attr('y', (d) => (d.index + offset) * (height / row))
        .attr('fill', settings.foregroundColor)
        .attr('font-size', `${fontSize}px`);
    if (isAnimate) {
        labels
            .append('animate')
            .attr('attributeName', 'fill-opacity')
            .attr('values', (d, i) => animateOpacity(i))
            .attr('dur', '3s')
            .attr('repeatCount', '1');
    }

    const arc = d3
        .arc<d3.PieArcDatum<type.LangInfo>>()
        .outerRadius(radius - margin)
        .innerRadius(radius / 2);

    // pie chart 생성
    const paths = group
        .append('g')
        .attr('transform', `translate(${radius}, ${radius})`)
        .selectAll(null)
        .data(pieData)
        .enter()
        .append('path')
        .attr('d', arc)
        .style('fill', (d) => d.data.color)
        .attr('stroke', settings.backgroundColor)
        .attr('stroke-width', '2px');
    paths
        .append('title')
        .text((d) => `${d.data.language} ${d.data.contributions}`);
    if (isAnimate) {
        paths
            .append('animate')
            .attr('attributeName', 'fill-opacity')
            .attr('values', (d, i) => animateOpacity(i))
            .attr('dur', '3s')
            .attr('repeatCount', '1');
    }
};
