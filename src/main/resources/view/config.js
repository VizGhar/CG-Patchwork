import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import { EndScreenModule } from './endscreen-module/EndScreenModule.js';
import { InteractiveDisplayModule } from './modules/InteractiveDisplayModule.js';
import { TooltipModule } from './tooltip-module/TooltipModule.js';
import { ToggleModule } from './toggle-module/ToggleModule.js'

export const modules = [
	GraphicEntityModule,
	EndScreenModule,
	InteractiveDisplayModule,
	TooltipModule,
	ToggleModule
];

export const options = [
  ToggleModule.defineToggle({
    toggle: 'tooltips',
    title: 'DEBUG',
    values: {
      'ON': true,
      'OFF': false
    },
    default: true
  })
]

export const playerColors = [
	'#FF7878',
	'#EEC373'
];