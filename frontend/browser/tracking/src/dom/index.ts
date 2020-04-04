export const enum NodeType {
  HTML = 1,
}

export const isHtmlElement = (target: EventTarget): target is HTMLElement => {
  return (
    ((target as unknown) as { nodeType: NodeType }).nodeType === NodeType.HTML
  );
};

type EncodedTagAndAttributes = (string | null)[];

export const encodeTarget = (
  target: EventTarget | null
): EncodedTagAndAttributes => {
  if (!target) {
    return [];
  }
  if (isHtmlElement(target)) {
    const values = [`<${target.nodeName}`] as (string | null)[];

    const pushAttributes = (name: string, value: string | null) => {
      values.push(`:${name}`);
      values.push(value);
    };

    if (target.getAttributeNames) {
      const attrs = target.getAttributeNames();
      for (let i = 0; i < attrs.length; i++) {
        const attributeName = attrs[i];
        const attributeValue = target.getAttribute(attributeName);
        pushAttributes(attributeName, attributeValue);
      }
    } else {
      for (let i = 0; i < target.attributes.length; i++) {
        const attribute = target.attributes[i];
        pushAttributes(attribute.name, attribute.value);
      }
    }
    return [];
  }
  return [];
};
