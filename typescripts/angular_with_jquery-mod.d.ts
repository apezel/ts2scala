// Type definitions for Angular JS 1.0
// Project: http://angularjs.org
// Definitions by: Diego Vilar <http://github.com/diegovilar>
// Definitions: https://github.com/borisyankov/DefinitelyTyped


/// <reference path="../jquery/jquery.d.ts" />

declare interface Event { }

declare interface HTMLElement { }

declare interface Node { } 

declare interface Element { }
/* ---------------- */

interface XMLHttpRequest {

}

interface JQueryAjaxSettings {
    accepts?: any;
    async?: boolean;
    beforeSend? (jqXHR: JQueryXHR, settings: JQueryAjaxSettings): any;
    cache?: boolean;
    complete? (jqXHR: JQueryXHR, textStatus: string): any;
    contents?: { [key: string]: any; };
    //According to jQuery.ajax source code, ajax's option actually allows contentType to set to "false"
    // https://github.com/borisyankov/DefinitelyTyped/issues/742
    contentType?: any;
    context?: any;
    converters?: { [key: string]: any; };
    crossDomain?: boolean;
    data?: any;
    dataFilter? (data: any, ty: any): any;
    dataType?: string;
    error? (jqXHR: JQueryXHR, textStatus: string, errorThrow: string): any;
    global?: boolean;
    headers?: { [key: string]: any; };
    ifModified?: boolean;
    isLocal?: boolean;
    jsonp?: string;
    jsonpCallback?: any;
    mimeType?: string;
    password?: string;
    processData?: boolean;
    scriptCharset?: string;
    statusCode?: { [key: string]: any; };
    success? (data: any, textStatus: string, jqXHR: JQueryXHR): any;
    timeout?: number;
    traditional?: boolean;
    type?: string;
    url?: string;
    username?: string;
    xhr?: any;
    xhrFields?: { [key: string]: any; };
}

/*
    Interface for the jqXHR object
*/
interface JQueryXHR extends XMLHttpRequest, JQueryPromise<any> {
    overrideMimeType(mimeType: string): any;
    abort(statusText?: string): void;
}

/*
    Interface for the JQuery callback
*/
interface JQueryCallback {
    add(...callbacks: any[]): any;
    disable(): any;
    empty(): any;
    fire(...arguments: any[]): any;
    fired(): boolean;
    fireWith(context: any, ...args: any[]): any;
    has(callback: any): boolean;
    lock(): any;
    locked(): boolean;
    remove(...callbacks: any[]): any;
}

/*
    Allows jQuery Promises to interop with non-jQuery promises
*/
interface JQueryGenericPromise<T> {
    then<U>(onFulfill: (value: T) => U, onReject?: (reason: any) => U): JQueryGenericPromise<U>;
    then<U>(onFulfill: (value: T) => JQueryGenericPromise<U>, onReject?: (reason: any) => U): JQueryGenericPromise<U>;
    then<U>(onFulfill: (value: T) => U, onReject?: (reason: any) => JQueryGenericPromise<U>): JQueryGenericPromise<U>;
    then<U>(onFulfill: (value: T) => JQueryGenericPromise<U>, onReject?: (reason: any) => JQueryGenericPromise<U>): JQueryGenericPromise<U>;
}

/*
    Interface for the JQuery promise, part of callbacks
*/
interface JQueryPromise<T> {
    always(...alwaysCallbacks: any[]): JQueryPromise<T>;
    done(...doneCallbacks: any[]): JQueryPromise<T>;
    fail(...failCallbacks: any[]): JQueryPromise<T>;
    progress(...progressCallbacks: any[]): JQueryPromise<T>;

    // Deprecated - given no typings
    pipe(doneFilter?: (x: any) => any, failFilter?: (x: any) => any, progressFilter?: (x: any) => any): JQueryPromise<any>;

    then<U>(onFulfill: (value: T) => U, onReject?: (...reasons: any[]) => U, onProgress?: (...progression: any[]) => any): JQueryPromise<U>;
    then<U>(onFulfill: (value: T) => JQueryGenericPromise<U>, onReject?: (...reasons: any[]) => U, onProgress?: (...progression: any[]) => any): JQueryPromise<U>;
    then<U>(onFulfill: (value: T) => U, onReject?: (...reasons: any[]) => JQueryGenericPromise<U>, onProgress?: (...progression: any[]) => any): JQueryPromise<U>;
    then<U>(onFulfill: (value: T) => JQueryGenericPromise<U>, onReject?: (...reasons: any[]) => JQueryGenericPromise<U>, onProgress?: (...progression: any[]) => any): JQueryPromise<U>;

    // Because JQuery Promises Suck
    then<U>(onFulfill: (...values: any[]) => U, onReject?: (...reasons: any[]) => U, onProgress?: (...progression: any[]) => any): JQueryPromise<U>;
    then<U>(onFulfill: (...values: any[]) => JQueryGenericPromise<U>, onReject?: (...reasons: any[]) => U, onProgress?: (...progression: any[]) => any): JQueryPromise<U>;
    then<U>(onFulfill: (...values: any[]) => U, onReject?: (...reasons: any[]) => JQueryGenericPromise<U>, onProgress?: (...progression: any[]) => any): JQueryPromise<U>;
    then<U>(onFulfill: (...values: any[]) => JQueryGenericPromise<U>, onReject?: (...reasons: any[]) => JQueryGenericPromise<U>, onProgress?: (...progression: any[]) => any): JQueryPromise<U>;
}

/*
    Interface for the JQuery deferred, part of callbacks
*/
interface JQueryDeferred<T> extends JQueryPromise<T> {
    always(...alwaysCallbacks: any[]): JQueryDeferred<T>;
    done(...doneCallbacks: any[]): JQueryDeferred<T>;
    fail(...failCallbacks: any[]): JQueryDeferred<T>;
    progress(...progressCallbacks: any[]): JQueryDeferred<T>;

    notify(...args: any[]): JQueryDeferred<T>;
    notifyWith(context: any, ...args: any[]): JQueryDeferred<T>;

    reject(...args: any[]): JQueryDeferred<T>;
    rejectWith(context: any, ...args: any[]): JQueryDeferred<T>;

    resolve(val: T): JQueryDeferred<T>;
    resolve(...args: any[]): JQueryDeferred<T>;
    resolveWith(context: any, ...args: any[]): JQueryDeferred<T>;
    state(): string;

    promise(target?: any): JQueryPromise<T>;
}

/*
    Interface of the JQuery extension of the W3C event object
*/

interface BaseJQueryEventObject extends Event {
    data: any;
    delegateTarget: Element;
    isDefaultPrevented(): boolean;
    isImmediatePropogationStopped(): boolean;
    isPropagationStopped(): boolean;
    namespace: string;
    preventDefault(): any;
    relatedTarget: Element;
    result: any;
    stopImmediatePropagation(): void;
    stopPropagation(): void;
    pageX: number;
    pageY: number;
    which: number;
    metaKey: boolean;
}

interface JQueryInputEventObject extends BaseJQueryEventObject {
    altKey: boolean;
    ctrlKey: boolean;
    /*metaKey: boolean;DEBUG*/
    shiftKey: boolean;
}

interface JQueryMouseEventObject extends JQueryInputEventObject {
    button: number;
    clientX: number;
    clientY: number;
    offsetX: number;
    offsetY: number;
    /*pageX: number;DEBUG*/
    /*pageY: number;DEBUG*/
    screenX: number;
    screenY: number;
}

interface JQueryKeyEventObject extends JQueryInputEventObject {
    char: any;
    charCode: number;
    key: any;
    keyCode: number;
}

interface JQueryPopStateEventObject extends BaseJQueryEventObject {
    /*originalEvent: PopStateEvent;*/
}

interface JQueryEventObject extends BaseJQueryEventObject, JQueryInputEventObject, JQueryMouseEventObject, JQueryKeyEventObject, JQueryPopStateEventObject {
}

/*
    Collection of properties of the current browser
*/

interface JQuerySupport {
    ajax?: boolean;
    boxModel?: boolean;
    changeBubbles?: boolean;
    checkClone?: boolean;
    checkOn?: boolean;
    cors?: boolean;
    cssFloat?: boolean;
    hrefNormalized?: boolean;
    htmlSerialize?: boolean;
    leadingWhitespace?: boolean;
    noCloneChecked?: boolean;
    noCloneEvent?: boolean;
    opacity?: boolean;
    optDisabled?: boolean;
    optSelected?: boolean;
    scriptEval? (): boolean;
    style?: boolean;
    submitBubbles?: boolean;
    tbody?: boolean;
}

interface JQueryParam {
    (obj: any): string;
    (obj: any, traditional: boolean): string;
}

/*
    Static members of jQuery (those on $ and jQuery themselves)
*/
interface JQueryStatic {

    /****
     AJAX
    *****/
    ajax(settings: JQueryAjaxSettings): JQueryXHR;
    ajax(url: string, settings?: JQueryAjaxSettings): JQueryXHR;

    ajaxPrefilter(dataTypes: string, handler: (opts: any, originalOpts: any, jqXHR: JQueryXHR) => any): any;
    ajaxPrefilter(handler: (opts: any, originalOpts: any, jqXHR: JQueryXHR) => any): any;

    ajaxSettings: JQueryAjaxSettings;

    ajaxSetup(): void;
    ajaxSetup(options: JQueryAjaxSettings): void;

    get(url: string, data?: any, success?: any, dataType?: any): JQueryXHR;
    getJSON(url: string, data?: any, success?: any): JQueryXHR;
    getScript(url: string, success?: any): JQueryXHR;

    param: JQueryParam;

    post(url: string, data?: any, success?: any, dataType?: any): JQueryXHR;

    // Callbacks
    Callbacks(flags?: string): JQueryCallback;

    // Core
    holdReady(hold: boolean): any;

    (selector: string, context?: any): JQuery;
    (element: Element): JQuery;
    (object: {}): JQuery;
    (elementArray: Element[]): JQuery;
    (object: JQuery): JQuery;
    (func: Function): JQuery;
    (array: any[]): JQuery;
    (): JQuery;

    noConflict(removeAll?: boolean): Object;

    when<T>(...deferreds: JQueryGenericPromise<T>[]): JQueryPromise<T>;
    when<T>(...deferreds: T[]): JQueryPromise<T>;
    when<T>(...deferreds: any[]): JQueryPromise<T>;

    // CSS
    css(e: any, propertyName: string, value?: any): any;
    css(e: any, propertyName: any, value?: any): any;
    cssHooks: { [key: string]: any; };
    cssNumber: any;

    // Data
    data(element: Element, key: string, value: any): any;
    data(element: Element, key: string): any;
    data(element: Element): any;

    dequeue(element: Element, queueName?: string): any;

    hasData(element: Element): boolean;

    queue(element: Element, queueName?: string): any[];
    queue(element: Element, queueName: string, newQueueOrCallback: any): JQuery;

    removeData(element: Element, name?: string): JQuery;

    // Deferred
    Deferred<T>(beforeStart?: (deferred: JQueryDeferred<T>) => any): JQueryDeferred<T>;

    // Effects
    fx: { tick: () => void; interval: number; stop: () => void; speeds: { slow: number; fast: number; }; off: boolean; step: any; };

    // Events
    proxy(fn: (...args: any[]) => any, context: any, ...args: any[]): any;
    proxy(context: any, name: string, ...args: any[]): any;

    Event: {
        (name: string, eventProperties?: any): JQueryEventObject;
        new (name: string, eventProperties?: any): JQueryEventObject;
    };

    // Internals
    error(message: any): JQuery;

    // Miscellaneous
    expr: any;
    fn: any;  //TODO: Decide how we want to type this
    isReady: boolean;

    // Properties
    support: JQuerySupport;

    // Utilities
    contains(container: Element, contained: Element): boolean;

    each(collection: any, callback: (indexInArray: any, valueOfElement: any) => any): any;
    each(collection: JQuery, callback: (indexInArray: number, valueOfElement: HTMLElement) => any): any;
    each<T>(collection: T[], callback: (indexInArray: number, valueOfElement: T) => any): any;

    extend(target: any, ...objs: any[]): any;
    extend(deep: boolean, target: any, ...objs: any[]): any;

    globalEval(code: string): any;

    grep<T>(array: T[], func: (elementOfArray: T, indexInArray: number) => boolean, invert?: boolean): T[];

    inArray<T>(value: T, array: T[], fromIndex?: number): number;

    isArray(obj: any): boolean;
    isEmptyObject(obj: any): boolean;
    isFunction(obj: any): boolean;
    isNumeric(value: any): boolean;
    isPlainObject(obj: any): boolean;
    isWindow(obj: any): boolean;
    isXMLDoc(node: Node): boolean;

    makeArray(obj: any): any[];

    map<T, U>(array: T[], callback: (elementOfArray: T, indexInArray: number) => U): U[];
    map(array: any, callback: (elementOfArray: any, indexInArray: any) => any): any;

    merge<T>(first: T[], second: T[]): T[];
    merge<T,U>(first: T[], second: U[]): any[];

    noop(): any;

    now(): number;

    parseJSON(json: string): any;

    //FIXME: This should return an XMLDocument
    parseXML(data: string): any;

    queue(element: Element, queueName: string, newQueue: any[]): JQuery;

    trim(str: string): string;

    type(obj: any): string;

    unique(arr: any[]): any[];

    /**
    * Parses a string into an array of DOM nodes.
    *
    * @param data HTML string to be parsed
    * @param context DOM element to serve as the context in which the HTML fragment will be created
    * @param keepScripts A Boolean indicating whether to include scripts passed in the HTML string
    */
    parseHTML(data: string, context?: HTMLElement, keepScripts?: boolean): any[];

    Animation(elem: any, properties: any, options: any): any;

}

/*
    The jQuery instance members
*/
interface JQuery {
    // AJAX
    ajaxComplete(handler: any): JQuery;
    ajaxError(handler: (event: any, jqXHR: any, settings: any, exception: any) => any): JQuery;
    ajaxSend(handler: (event: any, jqXHR: any, settings: any, exception: any) => any): JQuery;
    ajaxStart(handler: () => any): JQuery;
    ajaxStop(handler: () => any): JQuery;
    ajaxSuccess(handler: (event: any, jqXHR: any, settings: any, exception: any) => any): JQuery;

    load(url: string, data?: any, complete?: any): JQuery;

    serialize(): string;
    serializeArray(): any[];

    // Attributes
    addClass(classNames: string): JQuery;
    addClass(func: (index: any, currentClass: any) => string): JQuery;

    // http://api.jquery.com/addBack/
    addBack(selector?: string): JQuery;


    attr(attributeName: string): string;
    attr(attributeName: string, value: any): JQuery;
    attr(map: { [key: string]: any; }): JQuery;
    attr(attributeName: string, func: (index: any, attr: any) => any): JQuery;

    hasClass(className: string): boolean;

    html(): string;
    html(htmlString: string): JQuery;
    html(htmlContent: (index: number, oldhtml: string) => string): JQuery;
    html(obj: JQuery): JQuery;

    prop(propertyName: string): any;
    prop(propertyName: string, value: any): JQuery;
    prop(map: any): JQuery;
    prop(propertyName: string, func: (index: any, oldPropertyValue: any) => any): JQuery;

    removeAttr(attributeName: string): JQuery;

    removeClass(className?: string): JQuery;
    removeClass(func: (index: any, cls: any) => any): JQuery;

    removeProp(propertyName: string): JQuery;

    toggleClass(className: string, swtch?: boolean): JQuery;
    toggleClass(swtch?: boolean): JQuery;
    toggleClass(func: (index: any, cls: any, swtch: any) => any): JQuery;

    val(): any;
    val(value: string[]): JQuery;
    val(value: string): JQuery;
    val(value: number): JQuery;
    val(func: (index: any, value: any) => any): JQuery;

    // CSS
    css(propertyName: string): string;
    css(propertyNames: string[]): string;
    css(properties: any): JQuery;
    css(propertyName: string, value: any): JQuery;
    css(propertyName: any, value: any): JQuery;

    height(): number;
    height(value: number): JQuery;
    height(value: string): JQuery;
    height(func: (index: any, height: any) => any): JQuery;

    innerHeight(): number;
    innerHeight(value: number): JQuery;

    innerWidth(): number;
    innerWidth(value: number): JQuery;

    offset(): { left: number; top: number; };
    offset(coordinates: any): JQuery;
    offset(func: (index: any, coords: any) => any): JQuery;

    outerHeight(includeMargin?: boolean): number;
    outerHeight(value: number, includeMargin?: boolean): JQuery;

    outerWidth(includeMargin?: boolean): number;
    outerWidth(value: number, includeMargin?: boolean): JQuery;

    position(): { top: number; left: number; };

    scrollLeft(): number;
    scrollLeft(value: number): JQuery;

    scrollTop(): number;
    scrollTop(value: number): JQuery;

    width(): number;
    width(value: number): JQuery;
    width(value: string): JQuery;
    width(func: (index: any, height: any) => any): JQuery;

    // Data
    clearQueue(queueName?: string): JQuery;

    data(key: string, value: any): JQuery;
    data(obj: { [key: string]: any; }): JQuery;
    data(key?: string): any;

    dequeue(queueName?: string): JQuery;

    removeData(nameOrList?: any): JQuery;

    // Deferred
    promise(type?: any, target?: any): JQueryPromise<any>;

    // Effects
    animate(properties: any, duration?: any, complete?: Function): JQuery;
    animate(properties: any, duration?: any, easing?: string, complete?: Function): JQuery;
    animate(properties: any, options: { duration?: any; easing?: string; complete?: Function; step?: Function; queue?: boolean; specialEasing?: any; }): JQuery;

    delay(duration: number, queueName?: string): JQuery;

    fadeIn(duration?: any, callback?: any): JQuery;
    fadeIn(duration?: any, easing?: string, callback?: any): JQuery;

    fadeOut(duration?: any, callback?: any): JQuery;
    fadeOut(duration?: any, easing?: string, callback?: any): JQuery;

    fadeTo(duration: any, opacity: number, callback?: any): JQuery;
    fadeTo(duration: any, opacity: number, easing?: string, callback?: any): JQuery;

    fadeToggle(duration?: any, callback?: any): JQuery;
    fadeToggle(duration?: any, easing?: string, callback?: any): JQuery;

    finish(): JQuery;

    hide(duration?: any, callback?: any): JQuery;
    hide(duration?: any, easing?: string, callback?: any): JQuery;

    show(duration?: any, callback?: any): JQuery;
    show(duration?: any, easing?: string, callback?: any): JQuery;

    slideDown(duration?: any, callback?: any): JQuery;
    slideDown(duration?: any, easing?: string, callback?: any): JQuery;

    slideToggle(duration?: any, callback?: any): JQuery;
    slideToggle(duration?: any, easing?: string, callback?: any): JQuery;

    slideUp(duration?: any, callback?: any): JQuery;
    slideUp(duration?: any, easing?: string, callback?: any): JQuery;

    stop(clearQueue?: boolean, jumpToEnd?: boolean): JQuery;
    stop(queue?: any, clearQueue?: boolean, jumpToEnd?: boolean): JQuery;

    toggle(duration?: any, callback?: any): JQuery;
    toggle(duration?: any, easing?: string, callback?: any): JQuery;
    toggle(showOrHide: boolean): JQuery;

    // Events
    bind(eventType: string, eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    bind(eventType: string, eventData: any, preventBubble: boolean): JQuery;
    bind(eventType: string, preventBubble: boolean): JQuery;
    bind(...events: any[]): JQuery;

    blur(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    blur(handler: (eventObject: JQueryEventObject) => any): JQuery;

    change(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    change(handler: (eventObject: JQueryEventObject) => any): JQuery;

    click(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    click(handler: (eventObject: JQueryEventObject) => any): JQuery;

    dblclick(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    dblclick(handler: (eventObject: JQueryEventObject) => any): JQuery;

    delegate(selector: any, eventType: string, handler: (eventObject: JQueryEventObject) => any): JQuery;

    focus(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    focus(handler: (eventObject: JQueryEventObject) => any): JQuery;

    focusin(eventData: any, handler: (eventObject: JQueryEventObject) => any): JQuery;
    focusin(handler: (eventObject: JQueryEventObject) => any): JQuery;

    focusout(eventData: any, handler: (eventObject: JQueryEventObject) => any): JQuery;
    focusout(handler: (eventObject: JQueryEventObject) => any): JQuery;

    hover(handlerIn: (eventObject: JQueryEventObject) => any, handlerOut: (eventObject: JQueryEventObject) => any): JQuery;
    hover(handlerInOut: (eventObject: JQueryEventObject) => any): JQuery;

    keydown(eventData?: any, handler?: (eventObject: JQueryKeyEventObject) => any): JQuery;
    keydown(handler: (eventObject: JQueryKeyEventObject) => any): JQuery;

    keypress(eventData?: any, handler?: (eventObject: JQueryKeyEventObject) => any): JQuery;
    keypress(handler: (eventObject: JQueryKeyEventObject) => any): JQuery;

    keyup(eventData?: any, handler?: (eventObject: JQueryKeyEventObject) => any): JQuery;
    keyup(handler: (eventObject: JQueryKeyEventObject) => any): JQuery;

    load(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    load(handler: (eventObject: JQueryEventObject) => any): JQuery;

    mousedown(): JQuery;
    mousedown(eventData: any, handler: (eventObject: JQueryMouseEventObject) => any): JQuery;
    mousedown(handler: (eventObject: JQueryMouseEventObject) => any): JQuery;

    mouseevent(eventData: any, handler: (eventObject: JQueryMouseEventObject) => any): JQuery;
    mouseevent(handler: (eventObject: JQueryMouseEventObject) => any): JQuery;

    mouseenter(): JQuery;
    mouseenter(eventData: any, handler: (eventObject: JQueryMouseEventObject) => any): JQuery;
    mouseenter(handler: (eventObject: JQueryMouseEventObject) => any): JQuery;

    mouseleave(): JQuery;
    mouseleave(eventData: any, handler: (eventObject: JQueryMouseEventObject) => any): JQuery;
    mouseleave(handler: (eventObject: JQueryMouseEventObject) => any): JQuery;

    mousemove(): JQuery;
    mousemove(eventData: any, handler: (eventObject: JQueryMouseEventObject) => any): JQuery;
    mousemove(handler: (eventObject: JQueryMouseEventObject) => any): JQuery;

    mouseout(): JQuery;
    mouseout(eventData: any, handler: (eventObject: JQueryMouseEventObject) => any): JQuery;
    mouseout(handler: (eventObject: JQueryMouseEventObject) => any): JQuery;

    mouseover(): JQuery;
    mouseover(eventData: any, handler: (eventObject: JQueryMouseEventObject) => any): JQuery;
    mouseover(handler: (eventObject: JQueryMouseEventObject) => any): JQuery;

    mouseup(): JQuery;
    mouseup(eventData: any, handler: (eventObject: JQueryMouseEventObject) => any): JQuery;
    mouseup(handler: (eventObject: JQueryMouseEventObject) => any): JQuery;

    off(events?: string, selector?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    off(eventsMap: { [key: string]: any; }, selector?: any): JQuery;

    on(events: string, selector?: string, data?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    on(events: string, selector?: string, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    on(eventsMap: { [key: string]: any; }, selector?: any, data?: any): JQuery;

    one(events: string, selector?: any, data?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    one(eventsMap: { [key: string]: any; }, selector?: any, data?: any): JQuery;

    ready(handler: any): JQuery;

    resize(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    resize(handler: (eventObject: JQueryEventObject) => any): JQuery;

    scroll(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    scroll(handler: (eventObject: JQueryEventObject) => any): JQuery;

    select(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    select(handler: (eventObject: JQueryEventObject) => any): JQuery;

    submit(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    submit(handler: (eventObject: JQueryEventObject) => any): JQuery;

    trigger(eventType: string, ...extraParameters: any[]): JQuery;
    trigger(event: JQueryEventObject): JQuery;

    triggerHandler(eventType: string, ...extraParameters: any[]): Object;

    unbind(eventType?: string, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    unbind(eventType: string, fls: boolean): JQuery;
    unbind(evt: any): JQuery;

    undelegate(): JQuery;
    undelegate(selector: any, eventType: string, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    undelegate(selector: any, events: any): JQuery;
    undelegate(namespace: string): JQuery;

    unload(eventData?: any, handler?: (eventObject: JQueryEventObject) => any): JQuery;
    unload(handler: (eventObject: JQueryEventObject) => any): JQuery;

    // Internals
    context: Element;
    jquery: string;

    error(handler: (eventObject: JQueryEventObject) => any): JQuery;
    error(eventData: any, handler: (eventObject: JQueryEventObject) => any): JQuery;

    pushStack(elements: any[]): JQuery;
    pushStack(elements: any[], name: any, arguments: any): JQuery;

    // Manipulation
    after(...content: any[]): JQuery;
    after(func: (index: any) => any): JQuery;

    append(...content: any[]): JQuery;
    append(func: (index: any, html: any) => any): JQuery;

    appendTo(target: any): JQuery;

    before(...content: any[]): JQuery;
    before(func: (index: any) => any): JQuery;

    /*clone(withDataAndEvents?: boolean, deepWithDataAndEvents?: boolean): JQuery;DEBUG*/

    detach(selector?: any): JQuery;

    empty(): JQuery;

    insertAfter(target: any): JQuery;
    insertBefore(target: any): JQuery;

    prepend(...content: any[]): JQuery;
    prepend(func: (index: any, html: any) => any): JQuery;

    prependTo(target: any): JQuery;

    remove(selector?: any): JQuery;

    replaceAll(target: any): JQuery;

    replaceWith(func: any): JQuery;

    text(): string;
    text(textString: any): JQuery;
    text(textString: (index: number, text: string) => string): JQuery;

    toArray(): any[];

    unwrap(): JQuery;

    wrap(wrappingElement: any): JQuery;
    wrap(func: (index: any) => any): JQuery;

    wrapAll(wrappingElement: any): JQuery;

    wrapInner(wrappingElement: any): JQuery;
    wrapInner(func: (index: any) => any): JQuery;

    // Miscellaneous
    each(func: (index: any, elem: Element) => any): JQuery;

    get(index?: number): any;

    index(): number;
    index(selector: string): number;
    index(element: any): number;

    // Properties
    length: number;
    selector: string;
    [x: string]: any;
    [x: number]: HTMLElement;

    // Traversing
    add(selector: string, context?: any): JQuery;
    add(...elements: any[]): JQuery;
    add(html: string): JQuery;
    add(obj: JQuery): JQuery;

    children(selector?: any): JQuery;

    closest(selector: string): JQuery;
    closest(selector: string, context?: Element): JQuery;
    closest(obj: JQuery): JQuery;
    closest(element: any): JQuery;
    closest(selectors: any, context?: Element): any[];

    contents(): JQuery;

    end(): JQuery;

    eq(index: number): JQuery;

    filter(selector: string): JQuery;
    filter(func: (index: any) => any): JQuery;
    filter(element: any): JQuery;
    filter(obj: JQuery): JQuery;

    find(selector: string): JQuery;
    find(element: any): JQuery;
    find(obj: JQuery): JQuery;

    first(): JQuery;

    has(selector: string): JQuery;
    has(contained: Element): JQuery;

    is(selector: string): boolean;
    is(func: (index: any) => any): boolean;
    is(element: any): boolean;
    is(obj: JQuery): boolean;

    last(): JQuery;

    map(callback: (index: any, domElement: Element) => any): JQuery;

    next(selector?: string): JQuery;

    nextAll(selector?: string): JQuery;

    nextUntil(selector?: string, filter?: string): JQuery;
    nextUntil(element?: Element, filter?: string): JQuery;
    nextUntil(obj?: JQuery, filter?: string): JQuery;

    not(selector: string): JQuery;
    not(func: (index: any) => any): JQuery;
    not(element: any): JQuery;
    not(obj: JQuery): JQuery;

    offsetParent(): JQuery;

    parent(selector?: string): JQuery;

    parents(selector?: string): JQuery;

    parentsUntil(selector?: string, filter?: string): JQuery;
    parentsUntil(element?: Element, filter?: string): JQuery;
    parentsUntil(obj?: JQuery, filter?: string): JQuery;

    prev(selector?: string): JQuery;

    prevAll(selector?: string): JQuery;

    prevUntil(selector?: string, filter?: string): JQuery;
    prevUntil(element?: Element, filter?: string): JQuery;
    prevUntil(obj?: JQuery, filter?: string): JQuery;

    siblings(selector?: string): JQuery;

    slice(start: number, end?: number): JQuery;

    // Utilities

    queue(queueName?: string): any[];
    queue(queueName: string, newQueueOrCallback: any): JQuery;
    queue(newQueueOrCallback: any): JQuery;
}
declare module "jquery" {
    export = $;
}
declare var jQuery: JQueryStatic;
declare var $: JQueryStatic;


declare interface Document { }
declare interface Window { }

// Support for painless dependency injection
interface Function {
    $inject:string[];
}


declare var angular: ng.IAngularStatic;
///////////////////////////////////////////////////////////////////////////////
// ng module (angular.js)
///////////////////////////////////////////////////////////////////////////////
declare module ng {

    // All service providers extend this interface
    interface IServiceProvider {
        $get(): any;
    }

    ///////////////////////////////////////////////////////////////////////////
    // AngularStatic
    // see http://docs.angularjs.org/api
    ///////////////////////////////////////////////////////////////////////////
    interface IAngularStatic {
        bind(context: any, fn: Function, ...args: any[]): Function;
        bootstrap(element: string, modules?: any[]): auto.IInjectorService;
        bootstrap(element: JQuery, modules?: any[]): auto.IInjectorService;
        bootstrap(element: Element, modules?: any[]): auto.IInjectorService;
        bootstrap(element: Document, modules?: any[]): auto.IInjectorService;
        copy(source: any, destination?: any): any;
        element: IAugmentedJQueryStatic;
        equals(value1: any, value2: any): boolean;
        extend(destination: any, ...sources: any[]): any;
        forEach(obj: any, iterator: (value: any, key: any) => any, context?: any): any;
        fromJson(json: string): any;
        identity(arg?: any): any;
        injector(modules?: any[]): auto.IInjectorService;
        isArray(value: any): boolean;
        isDate(value: any): boolean;
        isDefined(value: any): boolean;
        isElement(value: any): boolean;
        isFunction(value: any): boolean;
        isNumber(value: any): boolean;
        isObject(value: any): boolean;
        isString(value: any): boolean;
        isUndefined(value: any): boolean;
        lowercase(str: string): string;
        /** construct your angular application
		official docs: Interface for configuring angular modules.
		see: http://docs.angularjs.org/api/angular.Module
		*/
        module(
            /** name of your module you want to create */
            name: string,
            /** name of modules yours depends on */
            requires?: string[],
            configFunction?: any): IModule;
        noop(...args: any[]): void;
        toJson(obj: any, pretty?: boolean): string;
        uppercase(str: string): string;
        version: {
            full: string;
            major: number;
            minor: number;
            dot: number;
            codename: string;
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // Module
    // see http://docs.angularjs.org/api/angular.Module
    ///////////////////////////////////////////////////////////////////////////
    interface IModule {
        animation(name: string, animationFactory: Function): IModule;
        animation(name: string, inlineAnnotadedFunction: any[]): IModule;
        animation(object: Object): IModule;
        /** configure existing services.
		Use this method to register work which needs to be performed on module loading
		 */
        config(configFn: Function): IModule;
        /** configure existing services.
		Use this method to register work which needs to be performed on module loading
		 */
        config(inlineAnnotadedFunction: any[]): IModule;
        constant(name: string, value: any): IModule;
        constant(object: Object): IModule;
        controller(name: string, controllerConstructor: Function): IModule;
        controller(name: string, inlineAnnotadedConstructor: any[]): IModule;
        controller(object : Object): IModule;
        directive(name: string, directiveFactory: (...params:any[])=> IDirective): IModule;
        directive(name: string, inlineAnnotadedFunction: any[]): IModule;
        directive(object: Object): IModule;
        factory(name: string, serviceFactoryFunction: Function): IModule;
        factory(name: string, inlineAnnotadedFunction: any[]): IModule;
        factory(object: Object): IModule;
        filter(name: string, filterFactoryFunction: Function): IModule;
        filter(name: string, inlineAnnotadedFunction: any[]): IModule;
        filter(object: Object): IModule;
        provider(name: string, serviceProviderConstructor: Function): IModule;
        provider(name: string, inlineAnnotadedConstructor: any[]): IModule;
        provider(object: Object): IModule;
        run(initializationFunction: Function): IModule;
        run(inlineAnnotadedFunction: any[]): IModule;
        service(name: string, serviceConstructor: Function): IModule;
        service(name: string, inlineAnnotadedConstructor: any[]): IModule;
        service(object: Object): IModule;
        value(name: string, value: any): IModule;
        value(object: Object): IModule;

        // Properties
        name: string;
        requires: string[];
    }

    ///////////////////////////////////////////////////////////////////////////
    // Attributes
    // see http://docs.angularjs.org/api/ng.$compile.directive.Attributes
    ///////////////////////////////////////////////////////////////////////////
    interface IAttributes {
        $set(name: string, value: any): void;
        $observe(name: string, fn:(value?:any)=>any):void;
        $attr: any;
    }

    ///////////////////////////////////////////////////////////////////////////
    // FormController
    // see http://docs.angularjs.org/api/ng.directive:form.FormController
    ///////////////////////////////////////////////////////////////////////////
    interface IFormController {
        $pristine: boolean;
        $dirty: boolean;
        $valid: boolean;
        $invalid: boolean;
        $error: any;
        $setDirty(): void;
        $setPristine(): void;
    }

    ///////////////////////////////////////////////////////////////////////////
    // NgModelController
    // see http://docs.angularjs.org/api/ng.directive:ngModel.NgModelController
    ///////////////////////////////////////////////////////////////////////////
    interface INgModelController {
        $render(): void;
        $setValidity(validationErrorKey: string, isValid: boolean): void;
        $setViewValue(value: string): void;

        // XXX Not sure about the types here. Documentation states it's a string, but
        // I've seen it receiving other types throughout the code.
        // Falling back to any for now.
        $viewValue: any;

        // XXX Same as avove
        $modelValue: any;

        $parsers: IModelParser[];
        $formatters: IModelFormatter[];
        $error: any;
        $pristine: boolean;
        $dirty: boolean;
        $valid: boolean;
        $invalid: boolean;
    }

    interface IModelParser {
        (value: any): any;
    }

    interface IModelFormatter {
        (value: any): any;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Scope
    // see http://docs.angularjs.org/api/ng.$rootScope.Scope
    ///////////////////////////////////////////////////////////////////////////
    interface IScope {
        $apply(): any;
        $apply(exp: string): any;
        $apply(exp: (scope: IScope) => any): any;

        $broadcast(name: string, ...args: any[]): IAngularEvent;
        $destroy(): void;
        $digest(): void;
        $emit(name: string, ...args: any[]): IAngularEvent;

        // Documentation says exp is optional, but actual implementaton counts on it
        $eval(expression: string): any;
        $eval(expression: (scope: IScope) => any): any;

        // Documentation says exp is optional, but actual implementaton counts on it
        $evalAsync(expression: string): void;
        $evalAsync(expression: (scope: IScope) => any): void;

        // Defaults to false by the implementation checking strategy
        $new(isolate?: boolean): IScope;

        $on(name: string, listener: (event: IAngularEvent, ...args: any[]) => any): Function;

        $watch(watchExpression: string, listener?: string, objectEquality?: boolean): Function;
        $watch(watchExpression: string, listener?: (newValue: any, oldValue: any, scope: IScope) => any, objectEquality?: boolean): Function;
        $watch(watchExpression: (scope: IScope) => any, listener?: string, objectEquality?: boolean): Function;
        $watch(watchExpression: (scope: IScope) => any, listener?: (newValue: any, oldValue: any, scope: IScope) => any, objectEquality?: boolean): Function;

        $watchCollection(watchExpression: string, listener: (newValue: any, oldValue: any, scope: IScope) => any): Function;
        $watchCollection(watchExpression: (scope: IScope) => any, listener: (newValue: any, oldValue: any, scope: IScope) => any): Function;

        $parent: IScope;

        $id: number;

        // Hidden members
        $$isolateBindings: any;
        $$phase: any;
    }

    interface IAngularEvent {
        targetScope: IScope;
        currentScope: IScope;
        name: string;
        preventDefault: Function;
        defaultPrevented: boolean;

        // Available only events that were $emit-ted
        stopPropagation?: Function;
    }

    ///////////////////////////////////////////////////////////////////////////
    // WindowService
    // see http://docs.angularjs.org/api/ng.$window
    ///////////////////////////////////////////////////////////////////////////
    interface IWindowService extends Window {}

    ///////////////////////////////////////////////////////////////////////////
    // BrowserService
    // TODO undocumented, so we need to get it from the source code
    ///////////////////////////////////////////////////////////////////////////
    interface IBrowserService {}

    ///////////////////////////////////////////////////////////////////////////
    // TimeoutService
    // see http://docs.angularjs.org/api/ng.$timeout
    ///////////////////////////////////////////////////////////////////////////
    interface ITimeoutService {
        (func: Function, delay?: number, invokeApply?: boolean): IPromise<any>;
        cancel(promise: IPromise<any>): boolean;
    }

    ///////////////////////////////////////////////////////////////////////////
    // FilterService
    // see http://docs.angularjs.org/api/ng.$filter
    // see http://docs.angularjs.org/api/ng.$filterProvider
    ///////////////////////////////////////////////////////////////////////////
    interface IFilterService {
        (name: string): Function;
    }

    interface IFilterProvider extends IServiceProvider {
        register(name: string, filterFactory: Function): IServiceProvider;
    }

    ///////////////////////////////////////////////////////////////////////////
    // LocaleService
    // see http://docs.angularjs.org/api/ng.$locale
    ///////////////////////////////////////////////////////////////////////////
    interface ILocaleService {
        id: string;

        // These are not documented
        // Check angular's i18n files for exemples
        NUMBER_FORMATS: ILocaleNumberFormatDescriptor;
        DATETIME_FORMATS: ILocaleDateTimeFormatDescriptor;
        pluralCat: (num: any) => string;
    }

    interface ILocaleNumberFormatDescriptor {
        DECIMAL_SEP: string;
        GROUP_SEP: string;
        PATTERNS: ILocaleNumberPatternDescriptor[];
        CURRENCY_SYM: string;
    }

    interface ILocaleNumberPatternDescriptor {
        minInt: number;
        minFrac: number;
        maxFrac: number;
        posPre: string;
        posSuf: string;
        negPre: string;
        negSuf: string;
        gSize: number;
        lgSize: number;
    }

    interface ILocaleDateTimeFormatDescriptor {
        MONTH: string[];
        SHORTMONTH: string[];
        DAY: string[];
        SHORTDAY: string[];
        AMPMS: string[];
        medium: string;
        short: string;
        fullDate: string;
        longDate: string;
        mediumDate: string;
        shortDate: string;
        mediumTime: string;
        shortTime: string;
    }

    ///////////////////////////////////////////////////////////////////////////
    // LogService
    // see http://docs.angularjs.org/api/ng.$log
    ///////////////////////////////////////////////////////////////////////////
    interface ILogService {
        debug: ILogCall;
        error: ILogCall;
        info: ILogCall;
        log: ILogCall;
        warn: ILogCall;
    }

    // We define this as separete interface so we can reopen it later for
    // the ngMock module.
    interface ILogCall {
        (...args: any[]): void;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ParseService
    // see http://docs.angularjs.org/api/ng.$parse
    ///////////////////////////////////////////////////////////////////////////
    interface IParseService {
        (expression: string): ICompiledExpression;
    }

    interface ICompiledExpression {
        (context: any, locals?: any): any;

        // If value is not provided, undefined is gonna be used since the implementation
        // does not check the parameter. Let's force a value for consistency. If consumer
        // whants to undefine it, pass the undefined value explicitly.
        assign(context: any, value: any): any;
    }

    ///////////////////////////////////////////////////////////////////////////
    // LocationService
    // see http://docs.angularjs.org/api/ng.$location
    // see http://docs.angularjs.org/api/ng.$locationProvider
    // see http://docs.angularjs.org/guide/dev_guide.services.$location
    ///////////////////////////////////////////////////////////////////////////
    interface ILocationService {
        absUrl(): string;
        hash(): string;
        hash(newHash: string): ILocationService;
        host(): string;
        path(): string;
        path(newPath: string): ILocationService;
        port(): number;
        protocol(): string;
        replace(): ILocationService;
        search(): any;
        search(parametersMap: any): ILocationService;
        search(parameter: string, parameterValue: any): ILocationService;
        url(): string;
        url(url: string): ILocationService;
    }

    interface ILocationProvider extends IServiceProvider {
        hashPrefix(): string;
        hashPrefix(prefix: string): ILocationProvider;
        html5Mode(): boolean;

        // Documentation states that parameter is string, but
        // implementation tests it as boolean, which makes more sense
        // since this is a toggler
        html5Mode(active: boolean): ILocationProvider;
    }

    ///////////////////////////////////////////////////////////////////////////
    // DocumentService
    // see http://docs.angularjs.org/api/ng.$document
    ///////////////////////////////////////////////////////////////////////////
    interface IDocumentService extends Document {}

    ///////////////////////////////////////////////////////////////////////////
    // ExceptionHandlerService
    // see http://docs.angularjs.org/api/ng.$exceptionHandler
    ///////////////////////////////////////////////////////////////////////////
    interface IExceptionHandlerService {
        (exception: Error, cause?: string): void;
    }

    ///////////////////////////////////////////////////////////////////////////
    // RootElementService
    // see http://docs.angularjs.org/api/ng.$rootElement
    ///////////////////////////////////////////////////////////////////////////
    interface IRootElementService extends JQuery {}

    ///////////////////////////////////////////////////////////////////////////
    // QService
    // see http://docs.angularjs.org/api/ng.$q
    ///////////////////////////////////////////////////////////////////////////
    interface IQService {
        all(promises: IPromise<any>[]): IPromise<any[]>;
        defer<T>(): IDeferred<T>;
        reject(reason?: any): IPromise<void>;
        when<T>(value: T): IPromise<T>;
    }

    interface IPromise<T> {
        then<TResult>(successCallback: (promiseValue: T) => IHttpPromise<TResult>, errorCallback?: (reason: any) => any): IPromise<TResult>;
        then<TResult>(successCallback: (promiseValue: T) => IPromise<TResult>, errorCallback?: (reason: any) => any): IPromise<TResult>;
        then<TResult>(successCallback: (promiseValue: T) => TResult, errorCallback?: (reason: any) => TResult): IPromise<TResult>;
    }

    interface IDeferred<T> {
        resolve(value?: T): void;
        reject(reason?: any): void;
        promise: IPromise<T>;
    }

    ///////////////////////////////////////////////////////////////////////////
    // AnchorScrollService
    // see http://docs.angularjs.org/api/ng.$anchorScroll
    ///////////////////////////////////////////////////////////////////////////
    interface IAnchorScrollService {
        (): void;
    }

    interface IAnchorScrollProvider extends IServiceProvider {
        disableAutoScrolling(): void;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CacheFactoryService
    // see http://docs.angularjs.org/api/ng.$cacheFactory
    ///////////////////////////////////////////////////////////////////////////
    interface ICacheFactoryService {
        // Lets not foce the optionsMap to have the capacity member. Even though
        // it's the ONLY option considered by the implementation today, a consumer
        // might find it useful to associate some other options to the cache object.
        //(cacheId: string, optionsMap?: { capacity: number; }): CacheObject;
        (cacheId: string, optionsMap?: { capacity: number; }): ICacheObject;

        // Methods bellow are not documented
        info(): any;
        get (cacheId: string): ICacheObject;
    }

    interface ICacheObject {
        info(): {
            id: string;
            size: number;

            // Not garanteed to have, since it's a non-mandatory option
            //capacity: number;
        };
        put(key: string, value?: any): void;
        get (key: string): any;
        remove(key: string): void;
        removeAll(): void;
        destroy(): void;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CompileService
    // see http://docs.angularjs.org/api/ng.$compile
    // see http://docs.angularjs.org/api/ng.$compileProvider
    ///////////////////////////////////////////////////////////////////////////
    interface ICompileService {
        (element: string, transclude?: ITemplateLinkingFunction, maxPriority?: number): ITemplateLinkingFunction;
        (element: Element, transclude?: ITemplateLinkingFunction, maxPriority?: number): ITemplateLinkingFunction;
        (element: JQuery, transclude?: ITemplateLinkingFunction, maxPriority?: number): ITemplateLinkingFunction;
    }

    interface ICompileProvider extends IServiceProvider {
        directive(name: string, directiveFactory: Function): ICompileProvider;

        // Undocumented, but it is there...
        directive(directivesMap: any): ICompileProvider;
    }

    interface ITemplateLinkingFunction {
        // Let's hint but not force cloneAttachFn's signature
        (scope: IScope, cloneAttachFn?: (clonedElement?: JQuery, scope?: IScope) => any): JQuery;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ControllerService
    // see http://docs.angularjs.org/api/ng.$controller
    // see http://docs.angularjs.org/api/ng.$controllerProvider
    ///////////////////////////////////////////////////////////////////////////
    interface IControllerService {
        // Although the documentation doesn't state this, locals are optional
        (controllerConstructor: Function, locals?: any): any;
        (controllerName: string, locals?: any): any;
    }

    interface IControllerProvider extends IServiceProvider {
        register(name: string, controllerConstructor: Function): void;
        register(name: string, dependencyAnnotadedConstructor: any[]): void;
    }

    ///////////////////////////////////////////////////////////////////////////
    // HttpService
    // see http://docs.angularjs.org/api/ng.$http
    ///////////////////////////////////////////////////////////////////////////
    interface IHttpService {
        // At least moethod and url must be provided...
        (config: IRequestConfig): IHttpPromise<any>;
        get (url: string, RequestConfig?: any): IHttpPromise<any>;
        delete (url: string, RequestConfig?: any): IHttpPromise<any>;
        head(url: string, RequestConfig?: any): IHttpPromise<any>;
        jsonp(url: string, RequestConfig?: any): IHttpPromise<any>;
        post(url: string, data: any, RequestConfig?: any): IHttpPromise<any>;
        put(url: string, data: any, RequestConfig?: any): IHttpPromise<any>;
        defaults: IRequestConfig;

        // For debugging, BUT it is documented as public, so...
        pendingRequests: any[];
    }

    // This is just for hinting.
    // Some opetions might not be available depending on the request.
    // see http://docs.angularjs.org/api/ng.$http#Usage for options explanations
    interface IRequestConfig {
        method: string;
        url: string;
        params?: any;

        // XXX it has it's own structure...  perhaps we should define it in the future
        headers?: any;

        cache?: any;
        timeout?: number;
        withCredentials?: boolean;

        // These accept multiple types, so let's defile them as any
        data?: any;
        transformRequest?: any;
        transformResponse?: any;
    }

    interface IHttpPromiseCallback<T> {
        (data: T, status: number, headers: (headerName: string) => string, config: IRequestConfig): void;
    }

    interface IHttpPromiseCallbackArg<T> {
        data?: T;
        status?: number;
        headers?: (headerName: string) => string;
        config?: IRequestConfig;
    }

    interface IHttpPromise<T> extends IPromise<T> {
        success(callback: IHttpPromiseCallback<T>): IHttpPromise<T>;
        error(callback: IHttpPromiseCallback<T>): IHttpPromise<T>;
        /*then<TResult>(successCallback: (response: IHttpPromiseCallbackArg<T>) => TResult, errorCallback?: (response: IHttpPromiseCallbackArg<T>) => any): IPromise<TResult>;
        then<TResult>(successCallback: (response: IHttpPromiseCallbackArg<T>) => IPromise<TResult>, errorCallback?: (response: IHttpPromiseCallbackArg<T>) => any): IPromise<TResult>; DEBUG*/
    }

    interface IHttpProvider extends IServiceProvider {
        defaults: IRequestConfig;
        interceptors: any[];
        responseInterceptors: any[];
    }

    ///////////////////////////////////////////////////////////////////////////
    // HttpBackendService
    // see http://docs.angularjs.org/api/ng.$httpBackend
    // You should never need to use this service directly.
    ///////////////////////////////////////////////////////////////////////////
    interface IHttpBackendService {
        // XXX Perhaps define callback signature in the future
        (method: string, url: string, post?: any, callback?: Function, headers?: any, timeout?: number, withCredentials?: boolean): void;
    }

    ///////////////////////////////////////////////////////////////////////////
    // InterpolateService
    // see http://docs.angularjs.org/api/ng.$interpolate
    // see http://docs.angularjs.org/api/ng.$interpolateProvider
    ///////////////////////////////////////////////////////////////////////////
    interface IInterpolateService {
        (text: string, mustHaveExpression?: boolean): IInterpolationFunction;
        endSymbol(): string;
        startSymbol(): string;
    }

    interface IInterpolationFunction {
        (context: any): string;
    }

    interface IInterpolateProvider extends IServiceProvider {
        startSymbol(): string;
        startSymbol(value: string): IInterpolateProvider;
        endSymbol(): string;
        endSymbol(value: string): IInterpolateProvider;
    }

    ///////////////////////////////////////////////////////////////////////////
    // RouteParamsService
    // see http://docs.angularjs.org/api/ng.$routeParams
    ///////////////////////////////////////////////////////////////////////////
    interface IRouteParamsService {}

    ///////////////////////////////////////////////////////////////////////////
    // TemplateCacheService
    // see http://docs.angularjs.org/api/ng.$templateCache
    ///////////////////////////////////////////////////////////////////////////
    interface ITemplateCacheService extends ICacheObject {}

    ///////////////////////////////////////////////////////////////////////////
    // RootScopeService
    // see http://docs.angularjs.org/api/ng.$rootScope
    ///////////////////////////////////////////////////////////////////////////
    interface IRootScopeService extends IScope {}

    ///////////////////////////////////////////////////////////////////////////
    // RouteService
    // see http://docs.angularjs.org/api/ng.$route
    // see http://docs.angularjs.org/api/ng.$routeProvider
    ///////////////////////////////////////////////////////////////////////////
    interface IRouteService {
        reload(): void;
        routes: any;

        // May not always be available. For instance, current will not be available
        // to a controller that was not initialized as a result of a route maching.
        current?: ICurrentRoute;
    }

    // see http://docs.angularjs.org/api/ng.$routeProvider#when for options explanations
    interface IRoute {
        controller?: any;
        name?: string;
        template?: string;
        templateUrl?: any;
        resolve?: any;
        redirectTo?: any;
        reloadOnSearch?: boolean;
    }

    // see http://docs.angularjs.org/api/ng.$route#current
    interface ICurrentRoute extends IRoute {
        locals: {
            $scope: IScope;
            $template: string;
        };

        params: any;
    }

    interface IRouteProvider extends IServiceProvider {
        otherwise(params: any): IRouteProvider;
        when(path: string, route: IRoute): IRouteProvider;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Directive
    // see http://docs.angularjs.org/api/ng.$compileProvider#directive
    // and http://docs.angularjs.org/guide/directive
    ///////////////////////////////////////////////////////////////////////////

    interface IDirective{
        priority?: number;
        template?: any;
        templateUrl?: any;
        replace?: boolean;
        transclude?: any;
        restrict?: string;
        scope?: any;
        link?: Function;
        compile?: Function;
        controller?: any; 
    }

    ///////////////////////////////////////////////////////////////////////////
    // angular.element
    // when calling angular.element, angular returns a jQuery object,
    // augmented with additional methods like e.g. scope.
    // see: http://docs.angularjs.org/api/angular.element
    ///////////////////////////////////////////////////////////////////////////
    interface IAugmentedJQueryStatic extends JQueryStatic {
        (selector: string, context?: any): IAugmentedJQuery;
        (element: Element): IAugmentedJQuery;
        (object: {}): IAugmentedJQuery;
        (elementArray: Element[]): IAugmentedJQuery;
        (object: JQuery): IAugmentedJQuery;
        (func: Function): IAugmentedJQuery;
        (array: any[]): IAugmentedJQuery;
        (): IAugmentedJQuery;
    }

    interface IAugmentedJQuery extends JQuery {
        // TODO: events, how to define?
        //$destroy

        find(selector: string): IAugmentedJQuery;
        find(element: any): IAugmentedJQuery;
        find(obj: JQuery): IAugmentedJQuery;

        controller(name: string): any;
        injector(): any;
        scope(): IScope;

        inheritedData(key: string, value: any): JQuery;
        inheritedData(obj: { [key: string]: any; }): JQuery;
        inheritedData(key?: string): any;


    }


    ///////////////////////////////////////////////////////////////////////////
    // AUTO module (angular.js)
    ///////////////////////////////////////////////////////////////////////////
    export module auto {

        ///////////////////////////////////////////////////////////////////////
        // InjectorService
        // see http://docs.angularjs.org/api/AUTO.$injector
        ///////////////////////////////////////////////////////////////////////
        interface IInjectorService {
            annotate(fn: Function): string[];
            annotate(inlineAnnotadedFunction: any[]): string[];
            get (name: string): any;
            instantiate(typeConstructor: Function, locals?: any): any;
            invoke(func: Function, context?: any, locals?: any): any;
        }

        ///////////////////////////////////////////////////////////////////////
        // ProvideService
        // see http://docs.angularjs.org/api/AUTO.$provide
        ///////////////////////////////////////////////////////////////////////
        interface IProvideService {
            // Documentation says it returns the registered instance, but actual
            // implementation does not return anything.
            // constant(name: string, value: any): any;
            constant(name: string, value: any): void;

            decorator(name: string, decorator: Function): void;
            decorator(name: string, decoratorInline: any[]): void;
            factory(name: string, serviceFactoryFunction: Function): ng.IServiceProvider;
            provider(name: string, provider: ng.IServiceProvider): ng.IServiceProvider;
            provider(name: string, serviceProviderConstructor: Function): ng.IServiceProvider;
            service(name: string, constructor: Function): ng.IServiceProvider;
            value(name: string, value: any): ng.IServiceProvider;
        }

    }

}
