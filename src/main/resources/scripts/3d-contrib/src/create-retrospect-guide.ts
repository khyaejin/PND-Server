import * as d3 from 'd3';
import * as type from './type';

export const createRetrospectGuide = async (
    svg: d3.Selection<SVGSVGElement, unknown, null, unknown>,
    retrospect: string,  // 이미 HTML로 작성된 회고 텍스트
    x: number,           // 사각형의 x 좌표
    y: number,           // 사각형의 y 좌표
    width: number,       // 사각형의 너비
    height: number,      // 사각형의 높이
    settings: type.RadarContribSettings,  // PieLangOnlySettings 인터페이스 사용
    isForcedAnimation: boolean      // 애니메이션 강제 적용 여부
): Promise<void> => {
    // 사각형 추가 (애니메이션 포함)
    const rect = svg.append('rect')
        .attr('x', x)
        .attr('y', y)
        .attr('width', width)
        .attr('height', height)
        .attr('fill', settings.radarColor)   // 배경 색상 설정
        .attr('stroke', settings.radarColor) // 테두리 색상 설정
        .attr('stroke-width', 2)
        .attr('opacity', 0.5);  // 기본 불투명도

    // 애니메이션 적용: 불투명도를 0.2~0.7로 3초 동안 반복
    rect.transition()
        .duration(3000)            // 애니메이션 시간 3초
        .ease(d3.easeLinear)       // 선형 애니메이션
        .attr('fill-opacity', 0.7) // 최대 불투명도
        .transition()              // 다시 불투명도를 최소로 줄임
        .duration(3000)
        .attr('fill-opacity', 0.2)
        .on('end', function repeat() {  // 무한 반복
            d3.select(this).transition()
                .duration(3000)
                .attr('fill-opacity', 0.7)
                .transition()
                .duration(3000)
                .attr('fill-opacity', 0.2)
                .on('end', repeat);   // 애니메이션 반복
        });

    // foreignObject를 이용하여 HTML을 SVG 내에 삽입 (텍스트 동적 크기 조절)
    const textGroup = svg.append('foreignObject')
        .attr('x', x + 10)                    // 텍스트의 x 좌표
        .attr('y', y + 10)                    // 텍스트의 y 좌표
        .attr('width', width - 20)            // 텍스트 너비
        .attr('height', height - 20)          // 텍스트 높이
        .html(`<div xmlns="http://www.w3.org/1999/xhtml" 
                  style="font-family: Arial; color: ${settings.foregroundColor}; overflow: hidden;">
                  ${retrospect}
               </div>`);

    // 텍스트 요소의 크기를 SVG 영역에 맞게 동적으로 조절
    const fontSize = calculateOptimalFontSize(svg, retrospect, width - 20, height - 20);
    textGroup.select('div').style('font-size', `${fontSize}px`);
};

// 텍스트 요소의 크기를 계산하는 함수
const calculateOptimalFontSize = (
    svg: d3.Selection<SVGSVGElement, unknown, null, unknown>,
    text: string,
    maxWidth: number,
    maxHeight: number
): number => {
    const testText = svg.append('text').text(text).style('visibility', 'hidden');
    let fontSize = 16; // 기본 폰트 크기 설정
    testText.style('font-size', `${fontSize}px`);

    // textNode가 제대로 SVG 텍스트 요소인지 확인
    let textNode = testText.node() as SVGTextElement | null;
    if (textNode) {
        // 텍스트가 주어진 크기에 맞을 때까지 폰트 크기를 줄임
        while (textNode.getBBox().width > maxWidth || textNode.getBBox().height > maxHeight) {
            fontSize -= 1;
            testText.style('font-size', `${fontSize}px`);
            textNode = testText.node() as SVGTextElement | null; // 노드를 다시 가져옴 (크기 변경 시 반영)
            if (!textNode) break; // null 확인
        }
    }

    testText.remove();
    return fontSize;
};
